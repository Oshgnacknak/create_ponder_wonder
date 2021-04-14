package de.oshgnacknak.create_ponder_wonder;

import de.oshgnacknak.create_ponder_wonder.commands.AllCommands;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreatePonderWonder.MODID)
public class CreatePonderWonder {
    public static final String MODID = "create_ponder_wonder";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final PonderRenderScheduler PONDER_RENDERER = new PonderRenderScheduler();

    public CreatePonderWonder() {
        MinecraftForge.EVENT_BUS.addListener(AllCommands::register);
    }

    public static void chat(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendChatMessage(msg);
        }
    }
}