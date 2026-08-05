package com.carpet.trencher.addition.mixin;

import com.carpet.trencher.addition.utils.CarpetTrencherAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void onShouldSpreadLiquid(Level level, BlockPos blockPos, BlockState blockState,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (!CarpetTrencherAdditionSettings.waterWallLavaProtection) {
            return;
        }
        if (!blockState.getFluidState().is(FluidTags.LAVA)) {
            return;
        }

        // 检查上方1格
        BlockPos above1 = blockPos.above();
        BlockState state1 = level.getBlockState(above1);
        // 检查上方2格
        BlockPos above2 = above1.above();
        BlockState state2 = level.getBlockState(above2);

        if (isValidWaterloggedStair(state1) || isValidWaterloggedStair(state2)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean isValidWaterloggedStair(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER) &&
                state.getBlock() == Blocks.STONE_STAIRS &&
                state.getValue(StairBlock.HALF) == Half.BOTTOM;
    }
}