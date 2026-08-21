package com.carpet.trencher.addition.commands.track;

import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;

//? if <= 1.21.10 {
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.MinecartHopper;
//? } else {
/*
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
*/
//? }

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TrackState {

    private TrackState() {}

    public enum ValueType{
        STRING_SET(TrackStateCodecs.STRING_SET_CODEC),
        VEC3(TrackStateCodecs.VEC3_CODEC),
        ROTATION(TrackStateCodecs.ROTATION_CODEC),
        BOOLEAN(TrackStateCodecs.BOOLEAN_CODEC),
        FLOAT(TrackStateCodecs.FLOAT_CODEC),
        DOUBLE(TrackStateCodecs.DOUBLE_CODEC),
        SHORT(TrackStateCodecs.SHORT_CODEC),
        INT(TrackStateCodecs.INT_CODEC),
        ITEM_STACK_LIST(TrackStateCodecs.ITEM_STACK_LIST_CODEC);

        private final StreamCodec<RegistryFriendlyByteBuf, ?> codec;

        ValueType(
                StreamCodec<RegistryFriendlyByteBuf, ?> codec
        ) {
            this.codec = codec;
        }

        @SuppressWarnings("unchecked")
        public void encode(
                RegistryFriendlyByteBuf buf,
                Object value
        ) {
            ((StreamCodec<RegistryFriendlyByteBuf, Object>) codec)
                    .encode(buf, value);
        }

        public Object decode(
                RegistryFriendlyByteBuf buf
        ) {
            return codec.decode(buf);
        }
    }

    public enum Type {
        ID(1,ValueType.INT),
        TAGS(2, ValueType.STRING_SET),
        POS(3, ValueType.VEC3),
        MOTION(4, ValueType.VEC3),
        ROTATION(5, ValueType.ROTATION),
        ON_GROUND(6, ValueType.BOOLEAN),
        //? if >= 1.21.5 {
        FALL_DISTANCE(7, ValueType.DOUBLE),
        //? } else {
        /*
        FALL_DISTANCE(7, ValueType.FLOAT),
        */
        //? }
        FUSE(8, ValueType.INT),
        ENABLED(9, ValueType.BOOLEAN),
        ITEMS(10, ValueType.ITEM_STACK_LIST);

        private final int id;
        private final ValueType valueType;

        Type(int id, ValueType valueType) {
            this.id = id;
            this.valueType = valueType;
        }

        public int id() {
            return id;
        }

        public ValueType valueType() {
            return valueType;
        }

        public static Type fromId(int id) {
            for (Type type : values()) {
                if (type.id == id) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Unknown track state type: " + id);

        }

    }

    public record Rotation(float x, float y) {}

    public static Map<Type, Object> getState(Entity entity) {

        Map<Type, Object> states = new LinkedHashMap<>();

        states.put(Type.ID, entity.getId());

        //? if <= 1.21.11 {
        states.put(Type.TAGS, entity.getTags());
        //? } else {
        /*
        states.put(Type.TAGS, entity.entityTags());
        */
        //? }

        states.put(Type.POS, entity.position());
        states.put(Type.MOTION, entity.getDeltaMovement());
        states.put(
                Type.ROTATION,
                new Rotation(entity.getXRot(), entity.getYRot())
        );

        if (entity instanceof PrimedTnt tnt) {
            states.put(Type.FUSE, tnt.getFuse());
        }

        if (entity instanceof AbstractMinecartContainer minecartContainer) {
            states.put(Type.ITEMS,
                    minecartContainer.getItemStacks().stream()
                            .filter(stack -> !stack.isEmpty())
                            .toList());
            if (entity instanceof MinecartHopper minecartHopper) {
                states.put(Type.ENABLED, minecartHopper.isEnabled());
            }
        }

        return states;

    }

    public static List<TrackUpdatePayload.State> toPayloadStates(
            Map<Type, Object> states
    ) {
        return states.entrySet()
                .stream()
                .map(entry -> new TrackUpdatePayload.State(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

}