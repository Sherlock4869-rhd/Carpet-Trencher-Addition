package com.carpet.trencher.addition.tick;

import com.carpet.trencher.addition.network.tick.TntRemovedPayload;
import com.carpet.trencher.addition.network.tick.TntTickingPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.PrimedTnt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public final class TntTickingManager {

    private static final Map<ServerLevel, WorldTickState> WORLDS = new WeakHashMap<>();

    private static final class WorldTickState {
        private final Map<UUID, Boolean> pendingUpdates = new HashMap<>();
        private final Map<UUID, Boolean> cache = new HashMap<>();
    }

    private TntTickingManager() {}

    private static WorldTickState getState(ServerLevel level) {
        return WORLDS.computeIfAbsent(level, key -> new WorldTickState());
    }

    public static void add(PrimedTnt tnt) {
        if (tnt.level() instanceof ServerLevel level) {
            WorldTickState state = getState(level);
            UUID uuid = tnt.getUUID();
            state.pendingUpdates.put(uuid, false);
            state.cache.put(uuid, false);
        }
    }

    public static void markTicked(PrimedTnt tnt) {
        if (tnt.level() instanceof ServerLevel level) {
            getState(level).pendingUpdates.put(tnt.getUUID(), true);
        }
    }

    public static void forEachChanged(ServerLevel level, BiConsumer<UUID, Boolean> consumer) {
        WorldTickState state = getState(level);
        state.pendingUpdates.forEach((uuid, ticking) -> {
            Boolean previous = state.cache.get(uuid);
            if (previous != null && ticking != previous) {
                consumer.accept(uuid, ticking);
                state.cache.put(uuid, ticking);
            }
        });
    }

    public static void reset(ServerLevel level) {
        WorldTickState state = getState(level);
        state.pendingUpdates.replaceAll((uuid, ticking) -> false);
    }

    public static void remove(PrimedTnt tnt) {
        if (tnt.level() instanceof ServerLevel level) {
            WorldTickState state = getState(level);
            UUID uuid = tnt.getUUID();
            state.pendingUpdates.remove(uuid);
            state.cache.remove(uuid);
        }
    }

    public static void sendTickingState(ServerLevel level, TntTickingPayload payload) {
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendRemoved(ServerLevel level, PrimedTnt tnt) {
        UUID uuid = tnt.getUUID();
        TntRemovedPayload payload = new TntRemovedPayload(level.dimension(), uuid);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

}