package com.carpet.trencher.addition;

import com.carpet.trencher.addition.network.track.TrackListPayload;
import com.carpet.trencher.addition.track.ClientTrackHud;
import com.carpet.trencher.addition.track.ClientTrackManager;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
    @Override
    public void onInitializeClient() {

        CarpetTrencherAdditionMod.LOGGER.info("Carpet Trencher Addition client initialized!");

        ClientPlayNetworking.registerGlobalReceiver(
                TrackUpdatePayload.TYPE,
                (payload, context) ->
                        ClientTrackManager.update(payload)
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TrackListPayload.TYPE,
                (payload, context) -> {
                    switch (payload.action()) {
                        case ADD -> ClientTrackManager.add(payload.entityUuids());
                        case REMOVE -> ClientTrackManager.remove(payload.entityUuid());
                        case CLEAR -> ClientTrackManager.clear();
                }
                }
        );

        //? if >= 1.21.6 {
        HudElementRegistry.addLast(TrackUpdatePayload.ID, ClientTrackHud::render);
        //? } elif >= 1.21.4 {
        /*
        HudLayerRegistrationCallback.EVENT.register(
                layeredDrawer -> layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        TrackUpdatePayload.ID,
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
                (handler, client) ->
                        ClientTrackManager.clear()
        );

    }
}
