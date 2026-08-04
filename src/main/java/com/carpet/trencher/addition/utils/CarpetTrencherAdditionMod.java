package com.carpet.trencher.addition.utils;

import net.fabricmc.api.ModInitializer;

public class CarpetTrencherAdditionMod implements ModInitializer {

    @Override
    public void onInitialize() {
            // 可选：打印一条日志，确认模组已被加载
            System.out.println("Trencher Addition mod loaded!");
            carpet.CarpetServer.manageExtension(new CarpetTrencherAdditionServer());
    }
}