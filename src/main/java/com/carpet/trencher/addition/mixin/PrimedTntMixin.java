package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.CarpetTrencherAdditionSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin{

    @Unique private static final double minMomentum1 = 0.00505050505050505;
    @Unique private static final double maxMomentum1 = 0.005050555555555556;
    @Unique private static final double minMomentum2 = 0.01;
    @Unique private static final double maxMomentum2 = 0.0100001;

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("TAIL")
    )
    private void onTntSpawn(Level level, double x, double y, double z, LivingEntity livingEntity, CallbackInfo ci) {
        PrimedTnt tnt = (PrimedTnt) (Object) this;
        Vec3 motion = tnt.getDeltaMovement();
        double vx = motion.x;
        double vz = motion.z;

        if (CarpetTrencherAdditionSettings.preventExtremeTntMomentum && (isBadMomentum(vx) || isBadMomentum(vz))) {
                vx = 0.02;
                vz = 0;
        }

        if (!CarpetTrencherAdditionSettings.preventExtremeTntMomentum) {
            double customX = CarpetTrencherAdditionSettings.tntInitialXVelocity;
            if (customX >= -0.02 && customX <= 0.02) {
                double customZ = Math.sqrt(0.02 * 0.02 - customX * customX);

                if (vz < 0) {
                    customZ = -customZ;
                }
                vx = customX;
                vz = customZ;
            }
        }
        tnt.setDeltaMovement(vx, motion.y, vz);
    }

    @Unique
    private static boolean isBadMomentum(double value) {
        double abs = Math.abs(value);
        return (abs >= minMomentum1 && abs <= maxMomentum1) || (abs >= minMomentum2 && abs <= maxMomentum2);
    }
}