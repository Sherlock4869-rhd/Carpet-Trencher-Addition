package com.carpet.trencher.addition.track;

import java.util.UUID;

public class ClientEntityColor {

    private ClientEntityColor() {}

    public static int generateColor(UUID uuid) {
        long hash = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();

        hash ^= hash >>> 30;
        hash *= 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 27;
        hash *= 0x94D049BB133111EBL;
        hash ^= hash >>> 31;

        float hue = ((hash >>> 40) & 0xFFFFFF) / (float) 0x1000000;

        float saturation = 0.85F;
        float brightness = 1.0F;

        return hsvToArgb(hue, saturation, brightness);
    }

    private static int hsvToArgb(float hue, float saturation, float brightness) {
        float h = hue * 6.0F;
        float c = brightness * saturation;
        float x = c * (1.0F - Math.abs(h % 2.0F - 1.0F));
        float m = brightness - c;

        float r;
        float g;
        float b;

        if (h < 1.0F) {
            r = c;
            g = x;
            b = 0.0F;
        } else if (h < 2.0F) {
            r = x;
            g = c;
            b = 0.0F;
        } else if (h < 3.0F) {
            r = 0.0F;
            g = c;
            b = x;
        } else if (h < 4.0F) {
            r = 0.0F;
            g = x;
            b = c;
        } else if (h < 5.0F) {
            r = x;
            g = 0.0F;
            b = c;
        } else {
            r = c;
            g = 0.0F;
            b = x;
        }

        int red = Math.round((r + m) * 255.0F);
        int green = Math.round((g + m) * 255.0F);
        int blue = Math.round((b + m) * 255.0F);

        return 0xFF000000
                | (red << 16)
                | (green << 8)
                | blue;
    }
}