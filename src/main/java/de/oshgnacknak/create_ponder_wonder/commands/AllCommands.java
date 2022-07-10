package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


@Mod.EventBusSubscriber(modid = CreatePonderWonder.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AllCommands {

	@SubscribeEvent
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