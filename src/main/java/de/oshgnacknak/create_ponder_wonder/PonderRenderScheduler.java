package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PonderRenderScheduler {

    private ExecutorService executorService;
    private boolean rendering;

    public void start(String basePath) {
        if (rendering) {
            CreatePonderWonder.chat("Error: cannot be done twice");
            throw new IllegalStateException("Cannot start rendering twice");
        }

        rendering = true;
        executorService = Executors.newSingleThreadExecutor();

        CreatePonderWonder.LOGGER.info("Started rendering ponders");
        CreatePonderWonder.chat("Started rendering ponders");

        executorService.submit(() ->
            renderAllPonders(basePath));
    }

    private void renderAllPonders(String basePath) {
        PonderRegistry.all
            .values()
            .stream()
            .map(PonderRegistry::compile)
            .flatMap(List::stream)
            .forEach(ponder -> saveFrames(ponder, basePath));

        CreatePonderWonder.LOGGER.info("All ponders rendered: {}", basePath);
        CreatePonderWonder.chat("All ponders rendered: " + basePath);

        finishRendering();
    }

    private void saveFrames(PonderScene ponder, String basePath) {
        try {
            Path path = getOutPath(ponder, basePath);

            for (PonderRenderer.RenderResult result : new PonderRenderer(ponder)) {
                Path out = path.resolve(String.format("%06d.png", result.frame));
                result.image.write(out);
                System.gc();
            }

            CreatePonderWonder.chat("Finished rendering Ponder: " + path);
            CreatePonderWonder.LOGGER.info("Finished rendering Ponder: {}", path);
        } catch (Exception e) {
            CreatePonderWonder.chat("Error: " + e.getMessage());
            CreatePonderWonder.LOGGER.error("Could not save image", e);
            e.printStackTrace();
        }
    }

    private Path getOutPath(PonderScene ponder, String basePath) throws IOException {
        Path path = Paths.get(
            basePath,
            CreatePonderWonder.MODID,
            ponder.getString("out"));
        Files.createDirectories(path);
        return path;
    }

    public void stop() {
        if (!rendering) {
            CreatePonderWonder.chat("Aleady stopped...");
            return;
        }

        CreatePonderWonder.LOGGER.warn("Stopping rendering ponders abruptly");
        CreatePonderWonder.chat("Stopping rendering ponders abruptly");

        try {
            executorService.shutdown();
            while (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

            finishRendering();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finishRendering() {
        executorService = null;
        rendering = false;
        System.gc();

        CreatePonderWonder.LOGGER.info("Stopped rendering ponders");
        CreatePonderWonder.chat("Stopped rendering ponders");
    }
}