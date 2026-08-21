package com.carpet.trencher.addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.carpet.trencher.addition.commands.CTACommand;
import com.carpet.trencher.addition.commands.track.TrackManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CarpetTrencherAdditionServer implements CarpetExtension {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();

    @Override
    public void onGameStarted() {
        CarpetTrencherAdditionMod.LOGGER.info("CTA loading settings");
        CarpetServer.settingsManager.parseSettingsClass(
                CarpetTrencherAdditionSettings.class
        );
    }

    @Override
    public String version() {
        return "CTA";
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        String path = "/assets/carpet-trencher-addition/lang/" + lang + ".json";
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) {
                return GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), MAP_TYPE);
            }
        } catch (Exception e) {
            CarpetTrencherAdditionMod.LOGGER.error("Failed to load translation file for {}: {}", lang, e.getMessage(), e);
        }
        return new HashMap<>();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext
    ) {
        CTACommand.register(dispatcher);
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayer player) {
        TrackManager.onPlayerLoggedOut(player);
    }

}