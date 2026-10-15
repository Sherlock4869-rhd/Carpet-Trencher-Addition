package com.carpet.trencher.addition.network.tick;

import com.carpet.trencher.addition.utils.NetworkUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record TntRemovedPayload(
        ResourceKey<Level> dimension,
        UUID uuid
) implements CustomPacketPayload {

    public static final Type<TntRemovedPayload> TYPE = new Type<>(NetworkUtils.payloadID("tnt_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TntRemovedPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        NetworkUtils.writeDimension(buf, payload.dimension());
                        buf.writeUUID(payload.uuid());
                    },
                    buf -> {
                        ResourceKey<Level> dimension = NetworkUtils.readDimension(buf);
                        UUID uuid = buf.readUUID();
                        return new TntRemovedPayload(dimension, uuid);
                    }
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}