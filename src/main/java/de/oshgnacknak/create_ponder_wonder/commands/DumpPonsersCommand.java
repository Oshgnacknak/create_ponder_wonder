package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.foundation.ponder.PonderWonderUI;
import com.simibubi.create.foundation.ponder.PonderScene;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.PonderIndexer;
import de.oshgnacknak.create_ponder_wonder.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DumpPonsersCommand implements Command<CommandSource> {

    private static final double SCALE = 1;
    private static final int MAX_FRAMES = 10;

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String path = StringArgumentType.getString(context, "path");

        List<PonderWonderUI> ponderUIs =
            PonderIndexer
            .getPonders()
            .map(PonderWonderUI::new)
            .collect(Collectors.toList());

        for (int frame = 0; frame < MAX_FRAMES; frame++) {
            for (PonderWonderUI ponderUI : ponderUIs) {
                renderPonderScene(path, ponderUI, frame);
                ponderUI.getActiveScene().getWorld().tick();
            }
        }
        return 0;
    }

    private void renderPonderScene(String basePath, PonderWonderUI ponderWonderUI, int frame) {
        try {
            Path path = Paths.get(
                basePath,
                CreatePonderWonder.MODID,
                ponderWonderUI.getActiveScene().getString("out"),
                String.format("%3d.png", frame));
            Files.createDirectories(path.getParent());

            RenderUtils.addRenderJob(
                Minecraft.getInstance().getWindow().getWidth(),
                Minecraft.getInstance().getWindow().getHeight(),
                SCALE,
                ponderWonderUI::ponderWonderRenderWindow,
                path,
                false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
