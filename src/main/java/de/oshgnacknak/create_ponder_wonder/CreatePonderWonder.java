package de.oshgnacknak.create_ponder_wonder;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreatePonderWonder.MODID)
public class CreatePonderWonder {
	public static final String MODID = "create_ponder_wonder";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final PonderRenderScheduler PONDER_RENDERER = new PonderRenderScheduler();

	public CreatePonderWonder() {
	}

	public static void chat(String msg) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.chat(msg);
		}
	}
}