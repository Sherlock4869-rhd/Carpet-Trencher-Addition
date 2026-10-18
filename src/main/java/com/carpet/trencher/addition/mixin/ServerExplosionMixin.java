package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.CarpetTrencherAdditionSettings;
import com.carpet.trencher.addition.helper.ServerExposion.ServerExplosionAccess;
import com.carpet.trencher.addition.helper.ServerExposion.ServerExplosionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

//? if >= 1.21.2 {
import net.minecraft.world.level.ServerExplosion;
//? } else {
/*
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
*/
//? }

//? if <= 1.21.1 {
/*
@Mixin(Explosion.class)
*/
//? } else {
@Mixin(ServerExplosion.class)
//? }

public abstract class ServerExplosionMixin implements ServerExplosionAccess {

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
    @Shadow @Final private ObjectArrayList<BlockPos> toBlow;
    @Shadow @Final private boolean fire;
    @Shadow @Final private RandomSource random;
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

                            Optional<Float> optional =
                                    this.damageCalculator.getBlockExplosionResistance(
                                            explosion, this.level, blockPos, blockState, fluidState
                                    );
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
        if (!CarpetTrencherAdditionSettings.optimizeTntPushEntity) {
            return;
        }
        new ServerExplosionHelper(this).optimizer();
        ci.cancel();
    }

    @Inject(
            method = "interactWithBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void optimizeItemMerge (List<BlockPos> list, CallbackInfo ci) {
        if (!CarpetTrencherAdditionSettings.optimizeExplosionItems) {
            return;
        }
        new ServerExplosionHelper(this).optimizeItemMerge(list);
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
        if (!CarpetTrencherAdditionSettings.optimizeTntPushEntity) {
            return;
        }
        new ServerExplosionHelper(this).optimizer();
        ci.cancel();
    }

    @Inject(
            method = "finalizeExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void optimizeItemMerge(boolean particles, CallbackInfo ci) {
        if (!CarpetTrencherAdditionSettings.optimizeExplosionItems) {
            return;
        }
        new ServerExplosionHelper(this).optimizeItemMerge(this.toBlow);
        if (this.fire) {
            for (BlockPos blockPos : this.toBlow) {
                if (this.random.nextInt(3) == 0
                        && this.level.getBlockState(blockPos).isAir()
                        && this.level.getBlockState(blockPos.below())
                        .isSolidRender(this.level, blockPos.below())) {
                    this.level.setBlockAndUpdate(
                            blockPos,
                            BaseFireBlock.getState(this.level, blockPos)
                    );
                }
            }
        }
        ci.cancel();
    }

    */
    //? }

    @Override
    public float cta$getRadius() {
        return this.radius;
    }

    @Override
    public ExplosionDamageCalculator cta$getDamageCalculator() {
        return this.damageCalculator;
    }

    @Override
    public Entity cta$getSource() {
        return this.source;
    }

    @Override
    public DamageSource cta$getDamageSource() {
        return this.damageSource;
    }

    @Override
    public Map<Player, Vec3> cta$getHitPlayers() {
        return this.hitPlayers;
    }

    //? if >= 1.21.2 {
    @Override
    public ServerLevel cta$getLevel() {
        return this.level;
    }

    @Override
    public Vec3 cta$getCenter() {
        return this.center;
    }
    //? } else {
    /*
    @Override
    public Level cta$getLevel() {
        return this.level;
    }

    @Override
    public double cta$getCenterX() {
        return this.x;
    }

    @Override
    public double cta$getCenterY() {
        return this.y;
    }

    @Override
    public double cta$getCenterZ() {
        return this.z;
    }
    */
    //? }

}