package com.carpet.trencher.addition.helper.FallingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AnvilBlock;

public class FallingBlockHelper {
    public static boolean shouldFalling(ServerLevel level, BlockPos pos) {
        return isAnvil(level, pos)
                && (isAnvil(level, pos.north())
                || isAnvil(level, pos.south())
                || isAnvil(level, pos.east())
                || isAnvil(level, pos.west()));
    }

    private static boolean isAnvil(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getBlock()  instanceof AnvilBlock;
    }
}