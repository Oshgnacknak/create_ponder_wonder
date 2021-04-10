package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.command.CommandSource;

public class StartRenderingCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String path = StringArgumentType.getString(context, "path");
        CreatePonderWonder.PONDER_RENDERER.start(path);
        return SINGLE_SUCCESS;
    }
}
