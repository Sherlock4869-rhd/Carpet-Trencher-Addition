package com.carpet.trencher.addition.utils;

import com.carpet.trencher.addition.CarpetTrencherAdditionMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

//? if <= 1.21.10 {
import net.minecraft.resources.ResourceLocation;
//? } else {
/*
import net.minecraft.resources.Identifier;
*/
//? }

public class NetworkUtils {

    public static <T extends CustomPacketPayload> void registerS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        //? if <= 1.21.11 {
        PayloadTypeRegistry.playS2C().register
        //? } else {
        /*
        PayloadTypeRegistry.clientboundPlay().register
        */
                //? }
                (type, codec);
    }
    //? if <= 1.21.10 {
    public static ResourceLocation payloadID(String s) {
        return ResourceLocation.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, s);
    }
    //? } else {
    /*
    public static Identifier payloadID(String s) {
        return Identifier.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, s);
    }
    */
    //? }

    public static void writeDimension(RegistryFriendlyByteBuf buf, ResourceKey<Level> dimension) {
        //? if <= 1.21.10 {
        buf.writeResourceLocation(dimension.location());
        //?} else {
        /*
        buf.writeIdentifier(dimension.identifier());
        */
        //?}
    }

    public static ResourceKey<Level> readDimension(RegistryFriendlyByteBuf buf) {
        //? if <= 1.21.10 {
        ResourceLocation location = buf.readResourceLocation();
        //?} else {
        /*
        Identifier location = buf.readIdentifier();
        */
        //?}
        return ResourceKey.create(Registries.DIMENSION, location);
    }

}

