package com.carpet.trencher.addition.tick;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientTntTickingManager {

    private static final Map<ResourceKey<Level>, Map<UUID, Boolean>> WORLDS = new HashMap<>();

    private ClientTntTickingManager() {}

    public static void setState(ResourceKey<Level> dimension, UUID uuid, boolean ticking) {
        WORLDS.computeIfAbsent(dimension, ignored -> new HashMap<>())
                .put(uuid, ticking);
    }

    public static boolean getState(ResourceKey<Level> dimension, UUID uuid) {
        Map<UUID, Boolean> states = WORLDS.get(dimension);
        return states != null && states.getOrDefault(uuid, false);
    }

    public static void remove(ResourceKey<Level> dimension, UUID uuid) {
        Map<UUID, Boolean> states = WORLDS.get(dimension);
        if (states == null) {
            return;
        }

        states.remove(uuid);
        if (states.isEmpty()) {
            WORLDS.remove(dimension);
        }
    }

    public static void clear() {
        WORLDS.clear();
    }

}