package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.utils.CarpetTrencherAdditionSettings;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PrimedTnt.class)
public class PrimeTntMixin {

    private static final double minMomentum1 = 0.00505050505050505;
    private static final double maxMomentum1 = 0.005050555555555556;
    private static final double minMomentum2 = 0.01;
    private static final double maxMomentum2 = 0.0100001;

    @Redirect(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/PrimedTnt;setDeltaMovement(DDD)V"
            )
    )
    private void preventExtremeTntMomentum(PrimedTnt tnt, double x, double y, double z) {
        if (CarpetTrencherAdditionSettings.preventExtremeTntMomentum && (isBadMomentum(x) || isBadMomentum(z))) {
                // 固定改为 X 正方向
                x = 0.02;
                z = 0;
        }

        tnt.setDeltaMovement(x, y, z);

        if (!CarpetTrencherAdditionSettings.preventExtremeTntMomentum) {

            double customX = CarpetTrencherAdditionSettings.tntInitialXVelocity;

            if (customX >= -0.02 && customX <= 0.02) {
                double customZ = Math.sqrt(0.02 * 0.02 - customX * customX);

                if (z < 0) {
                    customZ = -customZ;
                }

                x = customX;
                z = customZ;
            }
        }

        tnt.setDeltaMovement(x, y, z);
    }

    private static boolean isBadMomentum(double value) {
        double abs = Math.abs(value);

        return (abs >= minMomentum1 && abs <= maxMomentum1) || (abs >= minMomentum2 && abs <= maxMomentum2);
    }

}