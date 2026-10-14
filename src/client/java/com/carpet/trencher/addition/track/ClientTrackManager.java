package com.carpet.trencher.addition.track;

import com.carpet.trencher.addition.commands.track.TrackState;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class ClientTrackManager {

    public static final Map<UUID, Map<TrackState.Type, Object>> trackedEntities = new LinkedHashMap<>();
    public static final Set<UUID> trackOrder = new LinkedHashSet<>();
    public static final Map<UUID, Integer> entitiesColor = new HashMap<>();

    private ClientTrackManager() {}

    public static void update(TrackUpdatePayload payload) {
        for (TrackUpdatePayload.EntityUpdate entityUpdate : payload.entities()) {
            Map<TrackState.Type, Object> states = trackedEntities.computeIfAbsent(
                    entityUpdate.entityUuid(),
                    ignored -> new EnumMap<>(TrackState.Type.class)
            );
            for (TrackUpdatePayload.State state : entityUpdate.states()) {
                states.put(state.type(), state.value());
            }
        }
    }

    public static Map<TrackState.Type, Object> get(UUID entityUuid) {
        return trackedEntities.get(entityUuid);
    }

    public static Map<TrackState.Type, Object> get(Entity entity) {
        return get(entity.getUUID());
    }

    public static void add(List<UUID> entityUuids) {
        trackOrder.addAll(entityUuids);
        entityUuids.forEach(uuid -> entitiesColor.put(uuid, ClientEntityColor.generateColor(uuid)));
    }

    public static void remove(UUID entityUuid) {
        trackedEntities.remove(entityUuid);
        trackOrder.remove(entityUuid);
        entitiesColor.remove(entityUuid);
    }

    public static void clear() {
        trackedEntities.clear();
        trackOrder.clear();
        entitiesColor.clear();
    }

    public static boolean isTracked(UUID entityUuid) {
        return trackOrder.contains(entityUuid);
    }

    public static boolean isTracked(Entity entity) {
        return isTracked(entity.getUUID());
    }

    //? if >= 1.21.5 {
    public static Entity getEntity(UUID entityUuid) {
        if (entityUuid == null) {
            return null;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        return minecraft.level.getEntity(entityUuid);
    }
    //? } else {
    /*
    public static Entity getEntity(int entityId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        return minecraft.level.getEntity(entityId);
    }
    */
    //? }

}