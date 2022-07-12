package de.oshgnacknak.create_ponder_wonder;

import de.oshgnacknak.create_ponder_wonder.renderer.PonderRenderScheduler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;

@Mod(BuildConfig.MODID)
public class CreatePonderWonder {
	public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MODID);

	public static final PonderRenderScheduler PONDER_RENDERER = new PonderRenderScheduler();

	public CreatePonderWonder() {
		ImageIO.setUseCache(false);
	}

	public static void chat(String msg) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.chat(msg);
		}
	}
}