package com.carpet.trencher.addition.commands.track;

import com.carpet.trencher.addition.network.track.TrackListPayload;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import com.carpet.trencher.addition.utils.ComponentUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class TrackManager {

    private static final Map<UUID, Map<TrackState.Type, Object>> cache = new HashMap<>();
    private static final Map<UUID, Map<UUID, Map<TrackState.Type, Object>>> pendingUpdates = new HashMap<>();
    public static final Map<UUID, Map<UUID, Component>> playerTrackedEntities = new HashMap<>();
    public static final Map<UUID, Set<UUID>> entityTrackers = new HashMap<>();

    public static void startTracking(ServerPlayer tracker, Collection<? extends Entity> entities) {
        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities =
                playerTrackedEntities.computeIfAbsent(
                        trackerUuid, ignored -> new LinkedHashMap<>()
                );
        List<UUID> addedEntityUuids = new ArrayList<>();
        List<Component> selectedEntityNames = new ArrayList<>();

        for (Entity entity : entities) {
            selectedEntityNames.add(entity.getName());

            UUID entityUuid = entity.getUUID();
            if (trackedEntities.containsKey(entityUuid)) {
                continue;
            }

            trackedEntities.put(entityUuid, entity.getName());
            entityTrackers.computeIfAbsent(
                    entityUuid, ignored -> new HashSet<>()
            ).add(trackerUuid);
            ((EntityTrackingAccess) entity).cta$setTracked(true);
            addedEntityUuids.add(entityUuid);
            sendInitialTracking(tracker, entity);
        }

        sendTrackOrderAdd(tracker, addedEntityUuids);

        MutableComponent message = ComponentUtils.formatComponentList(
                "[CTA] Successfully tracked: ", selectedEntityNames
        );
        tracker.sendSystemMessage(message);
    }

    private static void sendTrackOrderAdd(ServerPlayer tracker, List<UUID> entityUuids) {
        if (entityUuids.isEmpty()) {
            return;
        }
        ServerPlayNetworking.send(tracker, TrackListPayload.add(entityUuids));
    }

    public static void listTrackedEntities(ServerPlayer tracker) {
        Map<UUID, Component> trackedEntities = playerTrackedEntities.get(tracker.getUUID());
        Collection<Component> nameList = trackedEntities == null ? null : trackedEntities.values();
        MutableComponent message =
                ComponentUtils.formatComponentList("[CTA] Tracked entities: ", nameList);
        tracker.sendSystemMessage(message);
    }

    public static void clearTrackedEntities(ServerPlayer tracker) {
        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.remove(trackerUuid);
        clearPendingUpdatesForTracker(trackerUuid);
        if (trackedEntities != null) {
            for (UUID entityUuid : trackedEntities.keySet()) {
                Set<UUID> trackerUuids = entityTrackers.get(entityUuid);
                if (trackerUuids == null) {
                    continue;
                }
                trackerUuids.remove(trackerUuid);
                if (trackerUuids.isEmpty()) {
                    entityTrackers.remove(entityUuid);
                    removeStateCache(entityUuid);
                }
            }
        }
        ServerPlayNetworking.send(tracker, TrackListPayload.clear());
    }

    public static void removeTrackedEntity(ServerPlayer tracker, Integer index) {
        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.get(trackerUuid);
        if (trackedEntities == null) {
            return;
        }
        List<UUID> entityUuids = new ArrayList<>(trackedEntities.keySet());
        int size = entityUuids.size();
        if (index < 0 || index >= size) {
            tracker.sendSystemMessage(
                    Component.literal("Please enter a number between 1 and " + size)
                            .withStyle(ChatFormatting.RED)
            );
            return;
        }
        UUID entityUuid = entityUuids.get(index);
        Component entityName = trackedEntities.remove(entityUuid);
        if (trackedEntities.isEmpty()) {
            playerTrackedEntities.remove(trackerUuid);
        }
        removePendingUpdate(trackerUuid, entityUuid);
        Set<UUID> trackerUuids = entityTrackers.get(entityUuid);
        if (trackerUuids != null) {
            trackerUuids.remove(trackerUuid);
            if (trackerUuids.isEmpty()) {
                entityTrackers.remove(entityUuid);
                removeStateCache(entityUuid);
            }
        }
        ServerPlayNetworking.send(tracker, TrackListPayload.remove(entityUuid));
        tracker.sendSystemMessage(
                Component.literal("[CTA] Stop tracking: ").append(entityName)
        );
    }

    public static void sendInitialTracking(ServerPlayer tracker, Entity entity) {
        UUID entityUuid = entity.getUUID();
        Map<TrackState.Type, Object> currentState = TrackState.getState(entity);
        cache.put(entityUuid, new HashMap<>(currentState));
        List<TrackUpdatePayload.State> states = TrackState.toPayloadStates(currentState);
        TrackUpdatePayload.EntityUpdate entityUpdate = new TrackUpdatePayload.EntityUpdate(entityUuid, states);
        ServerPlayNetworking.send(tracker, new TrackUpdatePayload(List.of(entityUpdate)));
    }

    public static void updateTracking(Entity entity) {
        UUID entityUuid = entity.getUUID();
        Set<UUID> trackerUuids = entityTrackers.get(entityUuid);
        if (trackerUuids == null || trackerUuids.isEmpty()) {
            return;
        }
        Map<TrackState.Type, Object> currentState = TrackState.getState(entity);
        Map<TrackState.Type, Object> cachedState = cache.get(entityUuid);
        if (cachedState == null) {
            cache.put(entityUuid, new HashMap<>(currentState));
            for (UUID trackerUuid : trackerUuids) {
                pendingUpdates.computeIfAbsent(
                        trackerUuid, ignored -> new LinkedHashMap<>()
                ).put(entityUuid, new LinkedHashMap<>(currentState));
            }
            return;
        }

        Map<TrackState.Type, Object> changedStates = new LinkedHashMap<>();

        for (Map.Entry<TrackState.Type, Object> entry : currentState.entrySet()) {
            TrackState.Type type = entry.getKey();
            Object value = entry.getValue();
            if (!Objects.equals(cachedState.get(type), value)) {
                changedStates.put(type, value);
                cachedState.put(type, value);
            }
        }
        if (changedStates.isEmpty()) {
            return;
        }
        for (UUID trackerUuid : trackerUuids) {
            Map<UUID, Map<TrackState.Type, Object>> trackerUpdates =
                    pendingUpdates.computeIfAbsent(
                            trackerUuid, ignored -> new LinkedHashMap<>()
                    );
            Map<TrackState.Type, Object> pendingStates =
                    trackerUpdates.computeIfAbsent(
                            entityUuid, ignored -> new LinkedHashMap<>()
                    );
            pendingStates.putAll(changedStates);
        }
    }

    public static void flushUpdates(ServerLevel level) {
        if (pendingUpdates.isEmpty()) {
            return;
        }
        Map<UUID, Map<UUID, Map<TrackState.Type, Object>>> updates = new HashMap<>(pendingUpdates);
        pendingUpdates.clear();
        MinecraftServer server = level.getServer();
        updates.forEach((trackerUuid, entityUpdates) -> {
            ServerPlayer tracker = server.getPlayerList().getPlayer(trackerUuid);
            if (tracker == null || entityUpdates.isEmpty()) {
                return;
            }
            List<TrackUpdatePayload.EntityUpdate> entities =
                    entityUpdates.entrySet().stream()
                            .map(entry -> {
                                List<TrackUpdatePayload.State> states =
                                        TrackState.toPayloadStates(entry.getValue());
                                return new TrackUpdatePayload.EntityUpdate(entry.getKey(), states);
                            }).toList();
            ServerPlayNetworking.send(tracker, new TrackUpdatePayload(entities));
        });
    }

    private static void removeStateCache(UUID entityUuid) {
        cache.remove(entityUuid);
        pendingUpdates.entrySet().removeIf(entry -> {
            entry.getValue().remove(entityUuid);
            return entry.getValue().isEmpty();
        });
    }

    private static void removePendingUpdate(UUID trackerUuid, UUID entityUuid) {
        Map<UUID, Map<TrackState.Type, Object>> trackerUpdates = pendingUpdates.get(trackerUuid);
        if (trackerUpdates == null) {
            return;
        }
        trackerUpdates.remove(entityUuid);
        if (trackerUpdates.isEmpty()) {
            pendingUpdates.remove(trackerUuid);
        }
    }
    private static void clearPendingUpdatesForTracker(UUID trackerUuid) {
        pendingUpdates.remove(trackerUuid);
    }

    public static void cleanup(Entity entity) {
        UUID entityUuid = entity.getUUID();
        removeStateCache(entityUuid);
        Set<UUID> trackerUuids = entityTrackers.remove(entityUuid);
        if (trackerUuids == null || trackerUuids.isEmpty()) {
            return;
        }
        MinecraftServer server = entity.level().getServer();
        for (UUID trackerUuid : trackerUuids) {
            Map<UUID, Component> trackedEntities = playerTrackedEntities.get(trackerUuid);
            Component entityName = null;
            if (trackedEntities != null) {
                entityName = trackedEntities.remove(entityUuid);
                if (trackedEntities.isEmpty()) {
                    playerTrackedEntities.remove(trackerUuid);
                }
            }
            if (server == null) {
                continue;
            }
            ServerPlayer tracker = server.getPlayerList().getPlayer(trackerUuid);
            if (tracker == null || entityName == null) {
                continue;
            }
            ServerPlayNetworking.send(tracker, TrackListPayload.remove(entityUuid));
            tracker.sendSystemMessage(
                    Component.literal("[CTA] Stopped tracking: ").append(entityName)
            );
        }
    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        UUID playerUuid = player.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.remove(playerUuid);
        clearPendingUpdatesForTracker(playerUuid);
        if (trackedEntities == null) {
            return;
        }
        for (UUID entityUuid : trackedEntities.keySet()) {
            Set<UUID> trackerUuids = entityTrackers.get(entityUuid);
            if (trackerUuids == null) {
                continue;
            }
            trackerUuids.remove(playerUuid);
            if (trackerUuids.isEmpty()) {
                entityTrackers.remove(entityUuid);
                removeStateCache(entityUuid);
            }
        }
    }
}