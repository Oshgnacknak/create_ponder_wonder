package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;

public class StartRenderingCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		String path = StringArgumentType.getString(context, "path");
		// CreatePonderWonder.PONDER_RENDERER.start(path);
		Minecraft.getInstance().progressTasks.add(() -> CreatePonderWonder.PONDER_RENDERER.start(path));
		return SINGLE_SUCCESS;
	}
}
