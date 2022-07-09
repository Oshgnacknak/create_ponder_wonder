package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraftforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class AllCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("pw")
            .then(literal("start")
            .then(argument("path", StringArgumentType.greedyString())
            .executes(new StartRenderingCommand()))));
        event.getDispatcher().register(
            literal("pw")
            .then(literal("stop")
            .executes(new StopRenderingCommand())));
    }
}