package com.carpet.trencher.addition.network.track;

import com.carpet.trencher.addition.CarpetTrencherAdditionMod;
import com.carpet.trencher.addition.commands.track.TrackStateCodecs;
import com.carpet.trencher.addition.commands.track.TrackState;
import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

//? if <= 1.21.10 {
import net.minecraft.resources.ResourceLocation;
//? } else {
/*
import net.minecraft.resources.Identifier;
*/
//? }

import java.util.List;
import java.util.UUID;

public record TrackUpdatePayload(UUID entityUuid, List<State> states) implements CustomPacketPayload {

    //? if <= 1.21.10 {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, "track_update");
    //? } else {
    /*
    public static final Identifier ID = Identifier.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, "track_update");
    */
    //? }

    public static final Type<TrackUpdatePayload> TYPE = new Type<>(NetworkUtils.payloadID("track_update"));

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
            ByteBufCodecs.collection(
                    java.util.ArrayList::new,
                    STATE_CODEC
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrackUpdatePayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        TrackStateCodecs.UUID_CODEC.encode(buf, payload.entityUuid());
                        STATE_LIST_CODEC.encode(buf, payload.states());
                    },

                    buf -> {
                        UUID entityUuid = TrackStateCodecs.UUID_CODEC.decode(buf);
                        List<State> states = STATE_LIST_CODEC.decode(buf);
                        return new TrackUpdatePayload(entityUuid, states);
                    }
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}