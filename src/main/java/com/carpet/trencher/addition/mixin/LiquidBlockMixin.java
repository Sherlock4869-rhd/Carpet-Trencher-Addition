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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @Inject(
            method = "shouldSpreadLiquid",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onShouldSpreadLiquid(Level level, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        // 检查规则是否开启
        if (!CarpetTrencherAdditionSettings.waterWallLavaProtection) {
            return;
        }
        // 只处理岩浆
        if (!blockState.getFluidState().is(FluidTags.LAVA)) {
            return;
        }

        // 检查上方方块
        BlockPos abovePos = blockPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // 条件：上方为含水石头楼梯，且为完整面朝下
        if (aboveState.getFluidState().is(FluidTags.WATER) && aboveState.getBlock() == Blocks.STONE_STAIRS && aboveState.getValue(StairBlock.HALF) == Half.BOTTOM) {
            cir.setReturnValue(true);
        }
        // 否则执行原方法（正常生成圆石/黑曜石）
    }
}