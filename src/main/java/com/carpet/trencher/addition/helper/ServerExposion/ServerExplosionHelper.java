package com.carpet.trencher.addition.helper.ServerExposion;

import com.carpet.trencher.addition.rules.optimizedTnt.LivingEntityPushCache;
import com.carpet.trencher.addition.rules.optimizedTnt.TntPushCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

//? if <= 1.21.10 {
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
//? } else {
/*
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
*/
//? }

//? if >= 1.21.2 {
import net.minecraft.world.level.ServerExplosion;
//? } else {
/*
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
*/
//? }

import java.util.*;

public final class ServerExplosionHelper {

    private final ServerExplosionAccess explosion;
    private final float radius;
    private final ExplosionDamageCalculator damageCalculator;
    private final Entity source;
    private final DamageSource damageSource;
    private final Map<Player, Vec3> hitPlayers;

    //? if >= 1.21.2 {
    private final ServerLevel level;
    private final Vec3 center;
    //? } else {
    /*
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    */
    //? }

    public ServerExplosionHelper(ServerExplosionAccess explosion) {
        this.explosion = explosion;
        this.radius = explosion.cta$getRadius();
        this.damageCalculator = explosion.cta$getDamageCalculator();
        this.source = explosion.cta$getSource();
        this.damageSource = explosion.cta$getDamageSource();
        this.hitPlayers = explosion.cta$getHitPlayers();
        this.level = explosion.cta$getLevel();

        //? if >= 1.21.2 {
        this.center = explosion.cta$getCenter();
        //? } else {
        /*
        this.x = explosion.cta$getCenterX();
        this.y = explosion.cta$getCenterY();
        this.z = explosion.cta$getCenterZ();
        */
        //? }
    }

    public void optimizer() {

        if (this.radius < 1.0E-5F) {
            return;
        }

        Vec3 center = getExplosionCenter();
        float radius = this.radius * 2.0F;
        int x0 = Mth.floor(center.x - radius - 1.0);
        int x1 = Mth.floor(center.x + radius + 1.0);
        int y0 = Mth.floor(center.y - radius - 1.0);
        int y1 = Mth.floor(center.y + radius + 1.0);
        int z0 = Mth.floor(center.z - radius - 1.0);
        int z1 = Mth.floor(center.z + radius + 1.0);

        TntPushCache tntPushCache = new TntPushCache();
        TntPushCache windChargePushCache = new TntPushCache();
        LivingEntityPushCache livingEntityPushCache = new LivingEntityPushCache();

        for (Entity entity : level.getEntities(this.source, new AABB( x0, y0, z0, x1, y1, z1))) {

            if (ignoreExplosion(entity)) {
                continue;
            }

            double distance = Math.sqrt(entity.distanceToSqr(center)) / radius;

            if (distance > 1.0) {
                continue;
            }

            switch (entity) {
                case PrimedTnt tnt ->
                        optimizeTnt(tnt, distance, center, tntPushCache);
                case AbstractWindCharge abstractWindCharge ->
                        optimizeProjectile(abstractWindCharge, distance, center, windChargePushCache);
                case LivingEntity livingEntity when !(entity instanceof Player) ->
                        optimizeLivingEntity(livingEntity, distance, center, livingEntityPushCache);
                default ->
                        optimizeEntity(entity, distance, center);
            }
        }
    }

    private void optimizeTnt(PrimedTnt tnt, double distance, Vec3 center, TntPushCache tntPushCache) {
        Vec3 position = tnt.position();
        Vec3 push = tntPushCache.get(position);
        if (push == null) {
            Vec3 direction = position.subtract(center).normalize();
            float multiplier = this.damageCalculator.getKnockbackMultiplier(tnt);
            float exposure = getSeenPercent(center, tnt);
            double power = (1.0 - distance) * exposure * multiplier;
            push = direction.scale(power);
            tntPushCache.put(position, push);
        }
        applyKnockback(tnt, push);
        tnt.onExplosionHit(this.source);
    }

    private void optimizeProjectile(Projectile projectile, double distance, Vec3 center, TntPushCache windChargePushCache) {
        Vec3 position = projectile.position();
        Vec3 push = windChargePushCache.get(position);

        if (push == null) {
            Vec3 direction = position.subtract(center).normalize();
            float multiplier = this.damageCalculator.getKnockbackMultiplier(projectile);
            float exposure = getSeenPercent(center, projectile);
            double power = (1.0 - distance) * exposure * multiplier;
            push = direction.scale(power);
            windChargePushCache.put(position, push);
        }
        applyKnockback(projectile, push);
        handlePostKnockback(projectile, push);
        projectile.onExplosionHit(this.source);
    }

    private void optimizeLivingEntity(LivingEntity livingEntity, double distance, Vec3 center, LivingEntityPushCache livingEntityPushCache) {

        LivingEntityPushCache.CacheValue value = livingEntityPushCache.get(livingEntity);

        if (value == null) {
            value = calculateExplosionResult(livingEntity, distance, center);
            livingEntityPushCache.put(livingEntity, value);
        }

        float damage = value.damage();
        Vec3 push = value.push();

        if (damage > 0.0F) {
            damageEntity(livingEntity, damage);
        }

        applyKnockback(livingEntity, push);
        livingEntity.onExplosionHit(this.source);

    }

    private void optimizeEntity(Entity entity, double distance, Vec3 center) {
        LivingEntityPushCache.CacheValue value = calculateExplosionResult(entity, distance, center);
        if (value.damage() > 0.0F) {
            damageEntity(entity, value.damage());
        }
        applyKnockback(entity, value.push());
        handlePostKnockback(entity, value.push());
        entity.onExplosionHit(this.source);

    }

    private LivingEntityPushCache.CacheValue calculateExplosionResult(Entity entity, double distance, Vec3 center) {
        Vec3 entityOrigin = entity.getEyePosition();
        Vec3 direction = entityOrigin.subtract(center).normalize();

        boolean shouldDamage = shouldDamageEntity(entity);
        float multiplier = this.damageCalculator.getKnockbackMultiplier(entity);

        float exposure = !shouldDamage && multiplier == 0.0F
                ? 0.0F
                : getSeenPercent(center, entity);

        float damage = shouldDamage
                ? getEntityDamageAmount(entity, exposure)
                : 0.0F;

        double knockbackResistance = entity instanceof LivingEntity livingEntity
                ? livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)
                : 0.0;

        double knockbackPower = (1.0 - distance) * exposure * multiplier * (1.0 - knockbackResistance);

        Vec3 push = direction.scale(knockbackPower);

        return new LivingEntityPushCache.CacheValue(push, damage);
    }

    private Vec3 getExplosionCenter() {
        //? if >= 1.21.2 {
        return this.center;
        //? } else {
        /*
        return new Vec3(this.x, this.y, this.z);
        */
        //? }
    }

    private boolean ignoreExplosion(Entity entity) {
        //? if >= 1.21.2 {
        return entity.ignoreExplosion((ServerExplosion) this.explosion);
        //? } else {
        /*
        return entity.ignoreExplosion((Explosion) this.explosion);
        */
        //? }
    }

    private float getSeenPercent(Vec3 center, Entity entity) {
        //? if >= 1.21.2 {
        return ServerExplosion.getSeenPercent(center, entity);
        //? } else {
        /*
        return Explosion.getSeenPercent(center, entity);
        */
        //? }
    }

    private boolean shouldDamageEntity(Entity entity) {
        //? if >= 1.21.2 {
        return this.damageCalculator.shouldDamageEntity((ServerExplosion) this.explosion, entity);
        //? } else {
        /*
        return this.damageCalculator.shouldDamageEntity((Explosion) this.explosion, entity);
        */
        //? }
    }

    private float getEntityDamageAmount(Entity entity, float exposure) {
        //? if >= 1.21.2 {
        return this.damageCalculator.getEntityDamageAmount((ServerExplosion) this.explosion, entity, exposure);
        //? } else {
        /*
        return this.damageCalculator.getEntityDamageAmount((Explosion)this.explosion, entity);
        */
        //? }
    }

    private void damageEntity(Entity entity, float damage) {
        //? if >= 1.21.2 {
        entity.hurtServer(this.level, this.damageSource, damage
        );
        //? } else {
        /*
        entity.hurt(this.damageSource, damage);
        */
        //? }
    }

    private void applyKnockback(Entity entity, Vec3 knockback) {
        //? if >= 1.21.2 {
        entity.push(knockback);
        //? } else {
        /*
        entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
        */
        //? }
    }

    private void handlePostKnockback(Entity entity, Vec3 knockback) {

        //? if >= 1.21.2 {
        if (isRedirectableProjectile(entity) && entity instanceof Projectile projectile) {
            projectile.setOwner(this.damageSource.getEntity());
        } else if (
                entity instanceof Player player
                        && !player.isSpectator()
                        && (!player.isCreative() || player.getAbilities().flying)
        ) {
            this.hitPlayers.put(player, knockback);
        }

        //? } else {
        /*
        if (
                entity instanceof Player player
                && !player.isSpectator()
                && (!player.isCreative() || player.getAbilities().flying)
        ) {
            this.hitPlayers.put(player, knockback);
        }
        */
        //? }
    }

    private boolean isRedirectableProjectile(Entity entity) {
        //? if <= 1.21.11 {
        return entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
        //? } else {
        /*
        return entity.is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
        */
        //? }
    }

    public void optimizeItemMerge(List<BlockPos> list) {
        Map<Item, Map<Integer, cta$StackCollector>> itemMap = new HashMap<>();
        Map<Integer, cta$StackCollector> linkedItemMap = new TreeMap<>();
        int[] index = {0};

        for (BlockPos blockPos : list) {
            level.getBlockState(blockPos).onExplosionHit(level, blockPos, (Explosion) this.explosion, (itemStack, blockPosx) ->
                    index[0] = cta$addOrAppendStack(linkedItemMap, itemMap, itemStack, blockPosx, index[0])
            );
        }

        itemMap.forEach((item, stackCollectors) ->
                linkedItemMap.putAll(stackCollectors)
        );

        for (cta$StackCollector stackCollector : linkedItemMap.values()) {
            Block.popResource(level, stackCollector.pos, stackCollector.stack);
        }
    }

    public static int cta$addOrAppendStack(
            Map<Integer, ServerExplosionHelper.cta$StackCollector> linkedItemMap,
            Map<Item, Map<Integer, ServerExplosionHelper.cta$StackCollector>> itemMap,
            ItemStack itemStack,
            BlockPos blockPos,
            int index
    ) {
        Item item = itemStack.getItem();
        Map<Integer, ServerExplosionHelper.cta$StackCollector> items = itemMap.get(item);
        if (itemStack.getCount() >= 16) {
            linkedItemMap.put(index, new cta$StackCollector(blockPos, itemStack));
            return ++index;
        }
        if (items == null) {
            Map<Integer, ServerExplosionHelper.cta$StackCollector> newItem = new HashMap<>();
            newItem.put(index, new cta$StackCollector(blockPos, itemStack));
            itemMap.put(item, newItem);
            return ++index;
        }
        Iterator<Map.Entry<Integer, cta$StackCollector>> iterator = items.entrySet().iterator();
        while (iterator.hasNext() && !itemStack.isEmpty()) {
            Map.Entry<Integer, ServerExplosionHelper.cta$StackCollector> entry = iterator.next();
            int stackIndex = entry.getKey();
            ServerExplosionHelper.cta$StackCollector stackCollector = entry.getValue();
            stackCollector.tryMerge(itemStack);
            if (stackCollector.stack.getCount() == 16) {
                linkedItemMap.put(stackIndex, stackCollector);
                iterator.remove();
            }
        }
        if (!itemStack.isEmpty()) {
            items.put(index, new cta$StackCollector(blockPos, itemStack));
            index++;
        }
        return index;
    }

    public static class cta$StackCollector {
        public final BlockPos pos;
        public ItemStack stack;

        cta$StackCollector(BlockPos blockPos, ItemStack itemStack) {
            this.pos = blockPos;
            this.stack = itemStack;
        }

        public void tryMerge(ItemStack itemStack) {
            if (ItemEntity.areMergable(this.stack, itemStack)) {
                this.stack = ItemEntity.merge(this.stack, itemStack, 16);
            }
        }
    }

}