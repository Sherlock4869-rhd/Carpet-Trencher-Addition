package com.carpet.trencher.addition.network.tick;

import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record TntRemovedPayload(UUID uuid) implements CustomPacketPayload {

    public static final Type<TntRemovedPayload> TYPE = new Type<>(NetworkUtils.payloadID("tnt_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TntRemovedPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeUUID(payload.uuid()),
                    buf -> new TntRemovedPayload(buf.readUUID())
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}