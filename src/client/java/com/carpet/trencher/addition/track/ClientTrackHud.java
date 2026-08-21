package com.carpet.trencher.addition.track;

import com.carpet.trencher.addition.commands.track.TrackState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.Entity;

//? if <= 1.21.11 {
import net.minecraft.client.gui.GuiGraphics;
//? } else {
/*
import net.minecraft.client.gui.GuiGraphicsExtractor;
*/
//? }

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ClientTrackHud {

    private ClientTrackHud() {}

    //? if <= 1.21.11 {
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    //? } else {
    /*
    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    */
    //? }

        //? if >= 1.21.8 {
        graphics.pose().pushMatrix();
        graphics.pose().scale(0.6F, 0.6F);
        //? } else {
        /*
        graphics.pose().pushPose();
        graphics.pose().scale(0.6F, 0.6F, 1.0F);
        */
        //? }

        int x = 10;
        int y = 10;

        int index = 1;
        for (UUID uuid : ClientTrackManager.trackOrder) {

            Map<TrackState.Type, Object> states = ClientTrackManager.trackedEntities.get(uuid);
            Minecraft minecraft = Minecraft.getInstance();

            if (states == null) {
                continue;
            }

            //? if >= 1.21.5 {
            Entity entity =
                    minecraft.level != null
                            ? ClientTrackManager.getEntity(uuid)
                            : null;
            //? } else {
            /*
            Object id = states.get(TrackState.Type.ID);
            Entity entity = null;
            if (id instanceof Integer entityId) {
                entity = ClientTrackManager.getEntity(entityId);
            }
            */
            //? }

            if (entity != null) {
                drawString(
                        graphics,
                        entity.getType().toString()
                                .replace("entity.minecraft.", index + ". minecraft:"),
                        x,
                        y,
                        ClientTrackManager.entitiesColor.get(uuid)
                );
            } else {
                drawString(graphics, index + ".Unknown", x, y, ClientTrackManager.entitiesColor.get(uuid));
            }
            index++;
            y += 12;

            for (Map.Entry<TrackState.Type, Object> state : states.entrySet()) {

                TrackState.Type type = state.getKey();
                Object value = state.getValue();

                if (type == TrackState.Type.TAGS && value instanceof Set<?> set) {
                    if (set.isEmpty()) {
                        continue;
                    }
                }

                drawString(graphics, formatState(type, value), x + 5, y);
                y += 12;

            }

            y += 6;

        }

        //? if >= 1.21.8 {
        graphics.pose().popMatrix();
        //? } else {
        /*
        graphics.pose().popPose();
        */
        //? }

    }

    private static String formatState(TrackState.Type type, Object value) {
        return switch (type) {
            case ID -> "ID: " + value;
            case POS -> "Pos: " + value;
            case MOTION -> "Motion: " + value;
            case ROTATION -> {
                TrackState.Rotation rotation = (TrackState.Rotation) value;
                yield "Rotation: (" + rotation.x() + "°, " + rotation.y() + "°)";
            }
            case ON_GROUND -> "On Ground: " + value;
            case FALL_DISTANCE -> "Fall Distance: " + value;
            case FUSE -> "Fuse: " + value;
            case ENABLED -> "Enabled: " + value;
            case TAGS -> "Tags: " + value;
            case ITEMS -> "Items: " + value;
        };
    }

    //? if <= 1.21.11 {
    private static void drawString(GuiGraphics graphics, String text, int x, int y) {
        drawString(graphics, text, x, y, 0xFFFFFFFF);
    }
    //? } else {
    /*
    private static void drawString(GuiGraphicsExtractor graphics, String text, int x, int y) {
        drawString(graphics, text, x, y, 0xFFFFFFFF);
    }
    */
    //? }

    //? if <= 1.21.11 {
    private static void drawString(GuiGraphics graphics, String text, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, text, x, y, color);
    }
    //? } else {
    /*
    private static void drawString(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        graphics.text(Minecraft.getInstance().font, text, x, y, color);
    }
    */
    //? }

}