package com.carpet.trencher.addition.network.track;

import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record TrackListPayload(
        Action action,
        UUID entityUuid,
        List<UUID> entityUuids
) implements CustomPacketPayload {

    public static final Type<TrackListPayload> TYPE = new CustomPacketPayload.Type<>(NetworkUtils.payloadID("track_order"));

    public enum Action {
        ADD,
        REMOVE,
        CLEAR
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TrackListPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeEnum(payload.action());
                        switch (payload.action()) {
                            case ADD -> {
                                buf.writeVarInt(payload.entityUuids().size());
                                for (UUID uuid : payload.entityUuids()) {
                                    buf.writeUUID(uuid);
                                }
                            }
                            case REMOVE -> buf.writeUUID(payload.entityUuid());
                            case CLEAR -> {}
                        }
                    },

                    buf -> {
                        Action action = buf.readEnum(Action.class);
                        return switch (action) {
                            case ADD -> {
                                int size = buf.readVarInt();
                                List<UUID> entityUuids = new ArrayList<>(size);
                                for (int i = 0; i < size; i++) {
                                    entityUuids.add(buf.readUUID());
                                }
                                yield new TrackListPayload(action, null, entityUuids);
                            }
                            case REMOVE -> new TrackListPayload(action, buf.readUUID(), List.of());
                            case CLEAR -> new TrackListPayload(action, null, List.of());
                        };
                    });

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static TrackListPayload add(List<UUID> uuids) {
        return new TrackListPayload(Action.ADD, null, List.copyOf(uuids));
    }

    public static TrackListPayload remove(UUID uuid) {
        return new TrackListPayload(Action.REMOVE, uuid, List.of());
    }

    public static TrackListPayload clear() {
        return new TrackListPayload(Action.CLEAR, null, List.of());
    }

}