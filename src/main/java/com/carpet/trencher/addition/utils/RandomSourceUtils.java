package com.carpet.trencher.addition.utils;

import net.minecraft.util.RandomSource;

//? if >= 1.21.2 {
import net.minecraft.server.level.ServerLevel;
//? } else {
/*
import net.minecraft.world.level.Level;
*/
//? }

public class RandomSourceUtils {
    public static RandomSource getRandom(
            //? if >= 1.21.2 {
            ServerLevel level
            //? } else {
            /*
            Level level
            */
            //? }
    ) {
        //? if <= 1.21.10 {
        return level.random;
        //? } else {
        /*
        return level.getRandom();
        */
        //? }
    }
}
