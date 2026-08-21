package com.carpet.trencher.addition.commands.track;

import com.carpet.trencher.addition.network.track.TrackListPayload;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import com.carpet.trencher.addition.utils.ComponentUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class TrackManager {

    public static final Map<UUID, Map<UUID, Component>> playerTrackedEntities = new HashMap<>();
    public static final Map<UUID, Set<UUID>> entityTrackers = new HashMap<>();

    public static void startTracking(ServerPlayer tracker, Collection<? extends Entity> entities) {

        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.computeIfAbsent(trackerUuid, uuid -> new LinkedHashMap<>());
        List<UUID> addedEntities = new ArrayList<>();

        entities.forEach(entity -> {
            UUID entityUuid = entity.getUUID();
            if (!trackedEntities.containsKey(entityUuid)) {
                trackedEntities.put(entityUuid, entity.getName());
                entityTrackers
                        .computeIfAbsent(entityUuid, uuid -> new HashSet<>())
                        .add(trackerUuid);
                ((TrackableEntity) entity).cta$setTracked(true);
                addedEntities.add(entityUuid);
            }
            sendTrackOrderAdd(tracker, addedEntities);
        });

        List<Component> nameList = entities.stream().map(Entity::getName).toList();
        MutableComponent message = ComponentUtils.formatComponentList("[CTA] Successfully tracked: ", nameList);

        tracker.sendSystemMessage(message);

    }

    private static void sendTrackOrderAdd(ServerPlayer tracker, List<UUID> entityUuids) {
        if (entityUuids.isEmpty()) {
            return;
        }
        ServerPlayNetworking.send(tracker, TrackListPayload.add(entityUuids));
    }

    public static void listTrackedEntities(ServerPlayer tracker) {
        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.get(trackerUuid);
        Collection<Component> nameList =
                trackedEntities == null
                        ? null :
                        trackedEntities.values();
        MutableComponent message = ComponentUtils.formatComponentList("[CTA] Tracked entities: ", nameList);
        tracker.sendSystemMessage(message);
    }

    public static void clearTrackedEntities(ServerPlayer tracker){

        UUID trackerUuid = tracker.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.remove(trackerUuid);

        if (trackedEntities != null) {
            trackedEntities.forEach((uuid, name) -> {
                Set<UUID> trackerUuids = entityTrackers.get(uuid);
                trackerUuids.remove(trackerUuid);
                if (trackerUuids.isEmpty()) {
                    entityTrackers.remove(uuid);
                }
            });
        }

        ServerPlayNetworking.send(tracker, TrackListPayload.clear());

    }

    public static void removeTrackedEntity(ServerPlayer tracker, Integer index) {

        UUID trackerUuid = tracker.getUUID();
        if (playerTrackedEntities.containsKey(trackerUuid)) {

            Map<UUID, Component> trackedEntities = playerTrackedEntities.get(trackerUuid);
            List<UUID> entityUuids = new ArrayList<>(trackedEntities.keySet());
            int size = entityUuids.size();
            if (index >= size) {

                tracker.sendSystemMessage(
                        Component
                                .literal("Please enter a number between 1 and " + size)
                                .withStyle(ChatFormatting.RED)
                );

            } else {

                UUID entityUuid = entityUuids.get(index);
                Component entityName = trackedEntities.remove(entityUuid);
                if(trackedEntities.isEmpty()) {
                    playerTrackedEntities.remove(trackerUuid);
                }

                Set<UUID> trackerUuids = entityTrackers.get(entityUuid);
                trackerUuids.remove(trackerUuid);
                if (trackerUuids.isEmpty()) {
                    entityTrackers.remove(entityUuid);
                }

                ServerPlayNetworking.send(tracker, TrackListPayload.remove(entityUuid));
                tracker.sendSystemMessage(Component.literal("[CTA]Stop tracking: ").append(entityName));

            }

        }

    }

    public static void updateTracking(Entity entity) {

        Set<UUID> trackerUuids = entityTrackers.get(entity.getUUID());

        if (trackerUuids == null || trackerUuids.isEmpty()) {
            return;
        }

        MinecraftServer server = entity.level().getServer();

        if (server == null) {
            return;
        }

        Map<TrackState.Type, Object> trackedStates = TrackState.getState(entity);

        for (UUID trackerUuid : trackerUuids) {

            ServerPlayer tracker = server.getPlayerList().getPlayer(trackerUuid);
            List<TrackUpdatePayload.State> states = TrackState.toPayloadStates(trackedStates);
            TrackUpdatePayload payload = new TrackUpdatePayload(entity.getUUID(), states);

            if (tracker != null) {
                ServerPlayNetworking.send(tracker, payload);
            }

        }
    }

    public static void cleanup(Entity entity) {

        UUID entityUuid = entity.getUUID();
        Set<UUID> trackerUuids = entityTrackers.remove(entityUuid);

        if (trackerUuids != null) {

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

                if (server != null) {
                    ServerPlayer tracker = server.getPlayerList().getPlayer(trackerUuid);
                    if (tracker != null && entityName != null) {
                        ServerPlayNetworking.send(tracker, TrackListPayload.remove(entityUuid));
                        tracker.sendSystemMessage(Component.literal("[CTA] Stopped tracking: ").append(entityName)
                        );
                    }
                }

            }

        }

    }

    public static void onPlayerLoggedOut(ServerPlayer player) {

        UUID playerUuid = player.getUUID();
        Map<UUID, Component> trackedEntities = playerTrackedEntities.remove(playerUuid);

        if (trackedEntities != null) {

            trackedEntities.forEach((uuid, name) -> {

                Set<UUID> trackerUuids = entityTrackers.get(uuid);

                if (trackerUuids != null) {
                    trackerUuids.remove(playerUuid);
                    if (trackerUuids.isEmpty()) {
                        entityTrackers.remove(uuid);
                    }
                }

            });
        }
    }
}