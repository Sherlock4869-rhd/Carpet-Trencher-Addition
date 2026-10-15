package com.carpet.trencher.addition;

import com.carpet.trencher.addition.network.tick.TntRemovedPayload;
import com.carpet.trencher.addition.network.tick.TntTickingPayload;
import com.carpet.trencher.addition.network.track.TrackListPayload;
import com.carpet.trencher.addition.tick.ClientTntTickingManager;
import com.carpet.trencher.addition.track.ClientTrackHud;
import com.carpet.trencher.addition.track.ClientTrackManager;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

//? if <= 1.21.10 {
import net.minecraft.resources.ResourceLocation;
//? } else {
/*
import net.minecraft.resources.Identifier;
*/
//? }

//? if >= 1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//? } elif >= 1.21.4 {
/*
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
*/
//? } else {
/*
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*/
//? }


public class CarpetTrencherAdditionClient implements ClientModInitializer {

    //? if <= 1.21.10 {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, "track_update");
    //? } else {
    /*
    public static final Identifier ID = Identifier.fromNamespaceAndPath(CarpetTrencherAdditionMod.MOD_ID, "track_update");
    */
    //? }

    @Override
    public void onInitializeClient() {

        CarpetTrencherAdditionMod.LOGGER.info("Carpet Trencher Addition client initialized!");

        ClientPlayNetworking.registerGlobalReceiver(
                TrackUpdatePayload.TYPE,
                (payload, context) -> context.client().execute(
                        () ->ClientTrackManager.update(payload)
                )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TrackListPayload.TYPE,
                (payload, context) -> context.client().execute(
                            () -> {switch (payload.action()) {
                                case ADD -> ClientTrackManager.add(payload.entityUuids());
                                case REMOVE -> ClientTrackManager.remove(payload.entityUuid());
                                case CLEAR -> ClientTrackManager.clear();
                            }}
                )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TntTickingPayload.TYPE,
                (payload, context) -> {
                    Minecraft client = context.client();
                    client.execute(() -> {
                        ClientLevel level = client.level;
                        ResourceKey<Level> dimension = payload.dimension();
                        if (level != null && level.dimension().equals(dimension)) {
                            payload.states().forEach((uuid, ticking) ->
                                    ClientTntTickingManager.setState(dimension, uuid, ticking)
                            );
                        }
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TntRemovedPayload.TYPE,
                (payload, context) -> {
                    Minecraft client = context.client();
                    client.execute(() -> {
                        ClientLevel level = client.level;
                        ResourceKey<Level> dimension = payload.dimension();
                        if (level != null && level.dimension().equals(dimension)) {
                            ClientTntTickingManager.remove(dimension, payload.uuid());
                        }
                    });
                }
        );

        //? if >= 1.21.6 {
        HudElementRegistry.addLast(ID, ClientTrackHud::render);
        //? } elif >= 1.21.4 {
        /*
        HudLayerRegistrationCallback.EVENT.register(
                layeredDrawer -> layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        ID,
                        ClientTrackHud::render
                )
        );
        */
        //? } else {
        /*
        HudRenderCallback.EVENT.register(ClientTrackHud::render);
        */
        //? }

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> {
                        ClientTrackManager.clear();
                        ClientTntTickingManager.clear();
                }
        );

    }
}