package com.carpet.trencher.addition.network.tick;

import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record TntTickingPayload(Map<UUID, Boolean> states)
        implements CustomPacketPayload {

    public static final Type<TntTickingPayload> TYPE =
            new Type<>(NetworkUtils.payloadID("track_ticking"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TntTickingPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeVarInt(payload.states().size());

                        payload.states().forEach((uuid, ticking) -> {
                            buf.writeUUID(uuid);
                            buf.writeBoolean(ticking);
                        });
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Map<UUID, Boolean> states = new HashMap<>();

                        for (int i = 0; i < size; i++) {
                            UUID uuid = buf.readUUID();
                            boolean ticking = buf.readBoolean();
                            states.put(uuid, ticking);
                        }

                        return new TntTickingPayload(states);
                    }
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}