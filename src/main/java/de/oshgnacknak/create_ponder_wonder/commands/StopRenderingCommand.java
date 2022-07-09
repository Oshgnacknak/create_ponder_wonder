package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.context.CommandContext;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.commands.CommandSourceStack;

public class StopRenderingCommand implements com.mojang.brigadier.Command<net.minecraft.commands.CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CreatePonderWonder.PONDER_RENDERER.stop();
        return SINGLE_SUCCESS;
    }
}
