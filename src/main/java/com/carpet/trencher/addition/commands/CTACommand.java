package com.carpet.trencher.addition.commands;

import com.carpet.trencher.addition.commands.track.TrackCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CTACommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal("cta")
                        .then(TrackCommand.create())
        );
    }
}