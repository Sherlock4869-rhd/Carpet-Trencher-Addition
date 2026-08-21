package com.carpet.trencher.addition.utils;

import com.carpet.trencher.addition.CarpetTrencherAdditionMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

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

}

