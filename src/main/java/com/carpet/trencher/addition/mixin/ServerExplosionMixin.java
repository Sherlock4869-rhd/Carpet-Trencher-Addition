package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.CarpetTrencherAdditionSettings;
import com.carpet.trencher.addition.rules.optimizedTnt.TntPushCache;
import com.carpet.trencher.addition.rules.optimizedTnt.LivingEntityPushCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

//? if >= 1.21.2 {
import net.minecraft.world.level.ServerExplosion;
//? } else {
/*
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.RandomSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
*/
//? }

//? if <= 1.21.1 {
/*
@Mixin(Explosion.class)
*/
//? } else {
@Mixin(ServerExplosion.class)
//? }

public abstract class ServerExplosionMixin {

    @Shadow @Final private float radius;
    @Shadow @Final private ExplosionDamageCalculator damageCalculator;
    @Shadow @Final private Map<Player, Vec3> hitPlayers;
    @Shadow @Final private Entity source;
    @Shadow @Final private DamageSource damageSource;

    //? if >= 1.21.2 {
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Vec3 center;
    //? } else {
    /*
    @Shadow @Final private Level level;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    */
    //? }

    //? if >= 1.21.2 {
    @Inject(
            method = "calculateExplodedPositions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCalculateExplodedPositions(CallbackInfoReturnable<List<BlockPos>> cir) {
        double value = CarpetTrencherAdditionSettings.explosionRayInit;
        if (value < 0 || value > 50) {
            return;
        }

        Set<BlockPos> set = new HashSet<>();
        float energy = this.radius * (float) value;

        for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = j / 15.0F * 2.0F - 1.0F;
                        double e = k / 15.0F * 2.0F - 1.0F;
                        double f = l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;

                        float h = energy;

                        double m = this.center.x;
                        double n = this.center.y;
                        double o = this.center.z;

                        while (h > 0.0F) {
                            BlockPos blockPos = BlockPos.containing(m, n, o);
                            BlockState blockState = this.level.getBlockState(blockPos);
                            FluidState fluidState = this.level.getFluidState(blockPos);
                            if (!this.level.isInWorldBounds(blockPos)) {
                                break;
                            }

                            Explosion explosion = (Explosion) this;

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(explosion, this.level, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.damageCalculator.shouldBlockExplode(explosion, this.level, blockPos, blockState, h)) {
                                set.add(blockPos);
                            }

                            m += d * 0.3F;
                            n += e * 0.3F;
                            o += f * 0.3F;
                            h -= 0.22500001F;
                        }
                    }
                }
            }
        }
        cir.setReturnValue(new ArrayList<>(set));
    }

    @Inject(
            method = "hurtEntities",
            at = @At("HEAD"),
            cancellable = true
    )
    private void optimizedTntEntityPush(CallbackInfo ci) {
        if (!CarpetTrencherAdditionSettings.optimizedTntPushEntity) {
            return;
        }
        optimizer();
        ci.cancel();
    }
    //? } else {
    /*
    @WrapOperation(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextFloat()F",
                    ordinal = 0
            )
    )
    private float onExplode(RandomSource random,Operation<Float> original) {
        double value = CarpetTrencherAdditionSettings.explosionRayInit;
        if (value < 0 || value > 50) {
            return original.call(random);
        }
        return (float) ((value - 0.7) / 0.6);
    }

    @Inject(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            cancellable = true
    )
    private void optimizedTntEntityPush(CallbackInfo ci) {
        if (!CarpetTrencherAdditionSettings.optimizedTntPushEntity) {
            return;
        }
        optimizer();
        ci.cancel();
    }
    */
    //? }

    @Unique
    private void optimizer() {

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
        LivingEntityPushCache livingEntityPushCache = new LivingEntityPushCache();

        for (Entity entity : level.getEntities(this.source, new AABB( x0, y0, z0, x1, y1, z1))) {

            if (ignoreExplosion(entity)) {
                continue;
            }

            double distance = Math.sqrt(entity.distanceToSqr(center)) / radius;

            if (distance > 1.0) {
                continue;
            }

            if (entity instanceof PrimedTnt tnt) {
                optimizeTnt(tnt, distance, center, tntPushCache);
                continue;
            } else if (entity instanceof LivingEntity livingEntity && !(entity instanceof Player)) {
                optimizeLivingEntity(livingEntity, distance, center, livingEntityPushCache);
                continue;
            }
            optimizeEntity(entity, distance, center);
        }
    }

    @Unique
    private void optimizeTnt(
            PrimedTnt tnt,
            double distance,
            Vec3 center,
            TntPushCache tntPushCache
    ) {
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

    @Unique
    private void optimizeLivingEntity(
            LivingEntity livingEntity,
            double distance,
            Vec3 center,
            LivingEntityPushCache livingEntityPushCache
    ) {

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

    @Unique
    private void optimizeEntity(Entity entity, double distance, Vec3 center) {

        LivingEntityPushCache.CacheValue value = calculateExplosionResult(entity, distance, center);

        if (value.damage() > 0.0F) {
            damageEntity(entity, value.damage());
        }

        applyKnockback(entity, value.push());
        handlePostKnockback(entity, value.push());
        entity.onExplosionHit(this.source);

    }

    @Unique
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

    @Unique
    private Vec3 getExplosionCenter() {
        //? if >= 1.21.2 {
        return this.center;
        //? } else {
        /*
        return new Vec3(this.x, this.y, this.z);
        */
        //? }
    }

    @Unique
    private boolean ignoreExplosion(Entity entity) {
        //? if >= 1.21.2 {
        return entity.ignoreExplosion((ServerExplosion) (Object) this);
        //? } else {
        /*
        return entity.ignoreExplosion((Explosion) (Object) this);
        */
        //? }
    }

    @Unique
    private float getSeenPercent(Vec3 center, Entity entity) {
        //? if >= 1.21.2 {
        return ServerExplosion.getSeenPercent(center, entity);
        //? } else {
        /*
        return Explosion.getSeenPercent(center, entity);
        */
        //? }
    }

    @Unique
    private boolean shouldDamageEntity(Entity entity) {
        //? if >= 1.21.2 {
        return this.damageCalculator.shouldDamageEntity((ServerExplosion) (Object) this, entity);
        //? } else {
        /*
        return this.damageCalculator.shouldDamageEntity((Explosion) (Object) this,entity);
        */
        //? }
    }

    @Unique
    private float getEntityDamageAmount(Entity entity, float exposure) {
        //? if >= 1.21.2 {
        return this.damageCalculator.getEntityDamageAmount((ServerExplosion) (Object) this, entity, exposure);
        //? } else {
        /*
        return this.damageCalculator.getEntityDamageAmount((Explosion) (Object) this,entity);
        */
        //? }
    }

    @Unique
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

    @Unique
    private void applyKnockback(Entity entity, Vec3 knockback) {
        //? if >= 1.21.2 {
        entity.push(knockback);
        //? } else {
        /*
        entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
        */
        //? }
    }

    @Unique
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

    @Unique
    private boolean isRedirectableProjectile(Entity entity) {
        //? if <= 1.21.11 {
        return entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
        //? } else {
        /*
        return entity.is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
        */
        //? }
    }
}