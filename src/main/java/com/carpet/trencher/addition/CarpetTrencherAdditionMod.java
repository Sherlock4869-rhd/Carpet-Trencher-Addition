package com.carpet.trencher.addition;

import com.carpet.trencher.addition.network.CTANetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetTrencherAdditionMod implements ModInitializer {
    public static final String MOD_ID = "carpet-trencher-addition";
    public static final Logger LOGGER = LoggerFactory.getLogger("carpet-trencher-addition");
    @Override
    public void onInitialize() {
        LOGGER.info("Trencher Addition mod loaded!");
        carpet.CarpetServer.manageExtension(new CarpetTrencherAdditionServer());
        CTANetworking.init();
    }
}