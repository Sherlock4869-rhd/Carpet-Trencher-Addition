package com.carpet.trencher.addition.commands.track;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TrackStateCodecs {

    private TrackStateCodecs() {}

    public static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID_CODEC =
            StreamCodec.of(
                    (buf, value) -> buf.writeUUID(value),
                    buf -> buf.readUUID()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, String> STRING_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.STRING_UTF8::encode,
                    ByteBufCodecs.STRING_UTF8::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3_CODEC =
            StreamCodec.of(
                    //? if >= 1.21.1 {
                    Vec3.STREAM_CODEC::encode,
                    Vec3.STREAM_CODEC::decode
                    //? } else {
                    /*
                    (buf, vec) -> {
                        buf.writeDouble(vec.x);
                        buf.writeDouble(vec.y);
                        buf.writeDouble(vec.z);
                        },
                    buf -> new Vec3(
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble()
                                )
                     */
                    //? }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrackState.Rotation> ROTATION_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeFloat(value.x());
                        buf.writeFloat(value.y());
                    },
                    buf -> new TrackState.Rotation(
                            buf.readFloat(),
                            buf.readFloat()
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Boolean> BOOLEAN_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.BOOL::encode,
                    ByteBufCodecs.BOOL::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Float> FLOAT_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.FLOAT::encode,
                    ByteBufCodecs.FLOAT::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Double> DOUBLE_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.DOUBLE::encode,
                    ByteBufCodecs.DOUBLE::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Short> SHORT_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.SHORT::encode,
                    ByteBufCodecs.SHORT::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Integer> INT_CODEC =
            StreamCodec.of(
                    ByteBufCodecs.INT::encode,
                    ByteBufCodecs.INT::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, java.util.Set<String>> STRING_SET_CODEC =
            ByteBufCodecs.collection(
                    java.util.HashSet::new,
                    STRING_CODEC
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> ITEM_STACK_LIST_CODEC =
            ByteBufCodecs.collection(
                    ArrayList::new,
                    ItemStack.STREAM_CODEC
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrackState.Type> TYPE_CODEC =
            StreamCodec.of(
                    (buf, type) -> buf.writeVarInt(type.id()),
                    buf -> TrackState.Type.fromId(buf.readVarInt())
            );

}