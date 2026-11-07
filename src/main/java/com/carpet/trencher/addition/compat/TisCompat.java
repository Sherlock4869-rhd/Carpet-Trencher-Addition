package com.carpet.trencher.addition.compat;

import java.lang.reflect.Field;

public final class TisCompat {

    private static final Field WET_EXPLOSION_FIELD = findWetExplosionField();

    private TisCompat() {}

    private static Field findWetExplosionField() {
        try {
            Class<?> settings = Class.forName(
                    "carpettisaddition.CarpetTISAdditionSettings"
            );

            return settings.getField("wetExplosionReintroduced");
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    public static boolean isWetExplosionReintroducedEnabled() {
        if (WET_EXPLOSION_FIELD == null) {
            return false;
        }
        try {
            return WET_EXPLOSION_FIELD.getBoolean(null);
        } catch (IllegalAccessException e) {
            return false;
        }
    }
}