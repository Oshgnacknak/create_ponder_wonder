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

    private static final int MAX_FRAMES = 10;
    private static final long MAX_PONDERS = 1;

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String path = StringArgumentType.getString(context, "path");

        List<PonderWonderUI> ponderUIs =
            PonderIndexer
            .getPonders()
            .limit(MAX_PONDERS)
            .map(PonderWonderUI::new)
            .collect(Collectors.toList());

        for (PonderWonderUI ponderUI : ponderUIs) {
            for (int frame = 0; frame < MAX_FRAMES; frame++) {
                renderPonderScene(path, ponderUI, frame);
                ponderUI.getActiveScene().tick();
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
                String.format("%06d.png", frame));
            Files.createDirectories(path.getParent());

            RenderUtils.addRenderJob(
                ponderWonderUI::ponderWonderRenderWindow,
                path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
