package de.oshgnacknak.create_ponder_wonder.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.foundation.ponder.PonderWonderUI;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.PonderIndexer;
import de.oshgnacknak.create_ponder_wonder.RenderUtils;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class DumpPonsersCommand implements Command<CommandSource> {

    private static final int FPS = 60;
    private static final int MAX_FRAMES = FPS*3;
    private static final long MAX_PONDERS = 1;

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String path = StringArgumentType.getString(context, "path");

        PonderIndexer
            .getPonders()
            .limit(MAX_PONDERS)
            .map(PonderWonderUI::new)
            .forEach(ui -> renderPonderUI(path, ui));

        return 0;
    }

    private void renderPonderUI(String basePath, PonderWonderUI ponderWonderUI) {
        try {
            for (int frame = 0; frame < MAX_FRAMES; frame++) {
                Promise<NativeImage> promise = renderUI(ponderWonderUI, frame);
                NativeImage img = promise.get();

                Path path = Paths.get(
                    basePath,
                    CreatePonderWonder.MODID,
                    ponderWonderUI.getActiveScene().getString("out"),
                    String.format("%06d.png", frame));

                Files.createDirectories(path.getParent());
                img.write(path);

                if (frame % 3 == 2) {
                    ponderWonderUI.tick();
                }
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            CreatePonderWonder.LOGGER.error("Could not save image", e);
        }
    }

    private Promise<NativeImage> renderUI(PonderWonderUI ponderWonderUI, int frame) {
        Promise<NativeImage> promise = GlobalEventExecutor.INSTANCE.newPromise();

        float pt = (frame % FPS) / (FPS / 3.0f);
        Minecraft.getInstance().field_213275_aU.add(() -> {
            try {
                NativeImage img = RenderUtils.render(ms ->
                    ponderWonderUI.ponderWonderRenderWindow(ms, pt));
                promise.setSuccess(img);
            } catch (Exception e) {
                promise.setFailure(e);
            }
        });

        return promise;
    }
}
