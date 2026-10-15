package com.carpet.trencher.addition.network.track;

import com.carpet.trencher.addition.commands.track.TrackState;
import com.carpet.trencher.addition.commands.track.TrackStateCodecs;
import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record TrackUpdatePayload(
        List<EntityUpdate> entities
) implements CustomPacketPayload {

    public static final Type<TrackUpdatePayload> TYPE = new Type<>(NetworkUtils.payloadID("track_update"));

    public record EntityUpdate(UUID entityUuid, List<State> states) {}

    public record State(TrackState.Type type, Object value) {}

    private static final StreamCodec<RegistryFriendlyByteBuf, State> STATE_CODEC =
            StreamCodec.of(
                    (buf, state) -> {
                        TrackState.Type type = state.type();
                        TrackStateCodecs.TYPE_CODEC.encode(buf, type);
                        type.valueType().encode(buf, state.value());
                    },
                    buf -> {
                        TrackState.Type type = TrackStateCodecs.TYPE_CODEC.decode(buf);
                        Object value = type.valueType().decode(buf);
                        return new State(type, value);
                    }
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<State>> STATE_LIST_CODEC =
            ByteBufCodecs.collection(ArrayList::new, STATE_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, EntityUpdate> ENTITY_UPDATE_CODEC =
            StreamCodec.of(
                    (buf, update) -> {
                        TrackStateCodecs.UUID_CODEC.encode(buf, update.entityUuid());
                        STATE_LIST_CODEC.encode(buf, update.states());
                    },
                    buf -> {
                        UUID entityUuid = TrackStateCodecs.UUID_CODEC.decode(buf);
                        List<State> states = STATE_LIST_CODEC.decode(buf);
                        return new EntityUpdate(entityUuid, states);
                    }
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<EntityUpdate>> ENTITY_LIST_CODEC =
            ByteBufCodecs.collection(ArrayList::new, ENTITY_UPDATE_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, TrackUpdatePayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> ENTITY_LIST_CODEC.encode(buf, payload.entities()),
                    buf -> new TrackUpdatePayload(ENTITY_LIST_CODEC.decode(buf))
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}