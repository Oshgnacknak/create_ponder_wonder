package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraftforge.event.RegisterCommandsEvent;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class AllCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("pw")
            .then(argument("path",
                StringArgumentType.greedyString()).executes(new DumpPonsersCommand()))
        );
    }
}