package com.carpet.trencher.addition.tick;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientTntTickingManager {
    private static final Map<UUID, Boolean> tickedTnts = new HashMap<>();

    private ClientTntTickingManager() {
    }

    public static void setState(UUID uuid, boolean ticking) {
        tickedTnts.put(uuid, ticking);
    }

    public static boolean getState(UUID uuid) {
        return tickedTnts.getOrDefault(uuid, false);
    }

    public static void remove(UUID uuid) {
        tickedTnts.remove(uuid);
    }

    public static void clear() {
        tickedTnts.clear();
    }
}