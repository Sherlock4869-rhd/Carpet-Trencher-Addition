package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.utils.CarpetTrencherAdditionSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuddingAmethystBlock.class)
public abstract class BuddingAmethystMixin {
    @Inject(
            method = "canClusterGrowAtState",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void disableWaterGrowth(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if ( CarpetTrencherAdditionSettings.disableAmethystWaterGrowth && blockState.is(Blocks.WATER)) {
            cir.setReturnValue(false);
        }
    }
}