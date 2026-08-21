package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.CarpetTrencherAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//? if >= 1.21.2 {
import net.minecraft.world.level.ServerExplosion;
//? } else {
/*
import net.minecraft.util.RandomSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
*/
//? }

//? if >= 1.21.2 {
@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {

    @Shadow @Final private float radius;
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Vec3 center;
    @Shadow @Final private ExplosionDamageCalculator damageCalculator;

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
}
//? } else {
/*
@Mixin(Explosion.class)
public class ServerExplosionMixin {
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
}
*/
//? }