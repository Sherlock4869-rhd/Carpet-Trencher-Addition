package com.carpet.trencher.addition.utils;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
        return "1.0.0";
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        String path = "/assets/carpet-trencher-addition/lang/" + lang + ".json";
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) {
                return GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), MAP_TYPE);
            }
        } catch (Exception e) {
            // 使用日志记录异常，替代 printStackTrace
            CarpetTrencherAdditionMod.LOGGER.error("Failed to load translation file for {}: {}", lang, e.getMessage(), e);
        }
        return new HashMap<>();
    }
}