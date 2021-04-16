package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.command.CommandSource;

public class StopRenderingCommand implements com.mojang.brigadier.Command<net.minecraft.command.CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) {
        CreatePonderWonder.PONDER_RENDERER.stop();
        return SINGLE_SUCCESS;
    }
}
