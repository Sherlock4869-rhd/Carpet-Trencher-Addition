package com.carpet.trencher.addition.commands.track;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class TrackCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("track")
                .then(
                        Commands.literal("list")
                                .executes(context -> {
                                    ServerPlayer tracker = context.getSource().getPlayerOrException();
                                    TrackManager.listTrackedEntities(tracker);
                                    return 1;
                                })
                )
                .then(
                        Commands.literal("clear")
                                .executes(context -> {
                                    ServerPlayer tracker = context.getSource().getPlayerOrException();
                                    TrackManager.clearTrackedEntities(tracker);
                                    return 1;
                                })
                )
                .then(
                        Commands.argument("target", EntityArgument.entities())
                                .executes(context -> {
                                    ServerPlayer tracker = context.getSource().getPlayerOrException();
                                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "target");
                                    TrackManager.startTracking(tracker, entities);
                                    return 1;
                                })
                )
                .then(
                        Commands.literal("remove")
                                .then(
                                        Commands.argument("ordinal", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            ServerPlayer tracker = context.getSource().getPlayerOrException();
                                            int ordinal = IntegerArgumentType.getInteger(context, "ordinal");
                                            TrackManager.removeTrackedEntity(tracker, ordinal - 1);
                                            return 1;
                                        })
                                )
                );
    }
}