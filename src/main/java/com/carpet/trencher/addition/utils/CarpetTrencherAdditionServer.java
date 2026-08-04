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
        System.out.println("CTA loading settings");
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
                // 指定 UTF-8 解码，避免乱码
                return GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), MAP_TYPE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若文件缺失，返回空 Map（Carpet 回退英文）
        return new HashMap<>();
    }
}