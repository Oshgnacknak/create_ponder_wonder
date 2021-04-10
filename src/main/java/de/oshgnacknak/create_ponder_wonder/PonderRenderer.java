package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderWonderUI;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PonderRenderer {

    private static final int FPS = 60;
    private static final int MAX_FRAMES = 15;
    private static final int MAX_PONDERS_AT_ONCE = 3;

    private final BlockingQueue<PonderWonderUI> pondersToRender;
    private final AtomicInteger currentlyRendering;
    private final Lock lock;
    private final Condition renderDone;

    private ExecutorService executorService;
    private boolean rendering;
    private boolean allEnqueued;
    private String basePath;

    public PonderRenderer() {
        pondersToRender = new ArrayBlockingQueue<>(MAX_PONDERS_AT_ONCE);
        currentlyRendering = new AtomicInteger();
        lock = new ReentrantLock();
        renderDone = lock.newCondition();
    }

    public void start(String basePath) {
        if (rendering) {
            CreatePonderWonder.chat("Error: cannot be done twice");
            throw new IllegalStateException("Cannot start rendering twice");
        }

        rendering = true;
        this.basePath = basePath;
        currentlyRendering.set(0);
        pondersToRender.clear();

        executorService = Executors.newCachedThreadPool();
        executorService.submit(this::renderPonders);
        executorService.submit(this::enqueueAllPonders);

        CreatePonderWonder.LOGGER.info("Started rendering ponders");
        CreatePonderWonder.chat("Started rendering ponders");
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
            rendering = false;

            CreatePonderWonder.LOGGER.info("Stopped rendering ponders");
            CreatePonderWonder.chat("Stopped rendering ponders");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void renderPonders() {
        try {
            while (rendering) {
                if (currentlyRendering.get() < MAX_PONDERS_AT_ONCE) {
                    PonderWonderUI ponder = pondersToRender.take();
                    currentlyRendering.incrementAndGet();
                    executorService.submit(() ->
                        renderPonder(ponder));
                } else {
                    waitForRenderDone();
                }

                if (allEnqueued) {
                    rendering = false;
                    CreatePonderWonder.LOGGER.info("All ponders rendered: {}", basePath);
                    CreatePonderWonder.chat("All ponders rendered: " + basePath);
                    return;
                }
            }
        } catch (Exception e) {
            CreatePonderWonder.chat("Error: " + e.getMessage());
            CreatePonderWonder.LOGGER.error("Exception whilst rendering ponders", e);
        }
    }

    private void waitForRenderDone() throws InterruptedException {
        lock.lock();
        try {
            renderDone.await();
        } finally {
            lock.unlock();
        }
    }

    private void renderPonder(PonderWonderUI ponder) {
        try {
            Path path = renderFrames(ponder);

            CreatePonderWonder.chat("Finished rendering Ponder: " + path);
            CreatePonderWonder.LOGGER.info("Finished rendering Ponder: {}", path);

        } catch (IOException | InterruptedException | ExecutionException e) {
            CreatePonderWonder.chat("Error: " + e.getMessage());
            CreatePonderWonder.LOGGER.error("Could not save image", e);
        } finally {
            signalRenderDone();
        }
    }

    private void signalRenderDone() {
        lock.lock();
        try {
            currentlyRendering.decrementAndGet();
            renderDone.signal();
        } finally {
            lock.unlock();
        }
    }

    private Path renderFrames(PonderWonderUI ponder) throws InterruptedException, ExecutionException, IOException {
        Path path = Paths.get(
            basePath,
            CreatePonderWonder.MODID,
            ponder.getActiveScene().getString("out"));
        Files.createDirectories(path);

        for (int frame = 0; frame < MAX_FRAMES; frame++) {
            Promise<NativeImage> promise = renderFrame(ponder, frame);
            NativeImage img = promise.get();

            Path out = path.resolve(String.format("%06d.png", frame));
            img.write(out);

            if (frame % 3 == 2) {
                ponder.tick();
            }
        }

        return path;
    }

    public Promise<NativeImage> renderFrame(PonderWonderUI ponder, int frame) {
        Promise<NativeImage> promise = GlobalEventExecutor.INSTANCE.newPromise();

        float pt = (frame % FPS) / (FPS / 3.0f);
        Minecraft.getInstance().field_213275_aU.add(() -> {
            try {
                NativeImage img = RenderUtils.render(ms ->
                    ponder.ponderWonderRenderWindow(ms, pt));
                promise.setSuccess(img);
            } catch (Exception e) {
                promise.setFailure(e);
            }
        });

        return promise;
    }

    private void enqueueAllPonders() {
        allEnqueued = false;
        Iterable<PonderWonderUI> ponders = getPonders();

        try {
            for (PonderWonderUI ponder : ponders) {
                pondersToRender.put(ponder);
            }
            allEnqueued = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Iterable<PonderWonderUI> getPonders() {
        return PonderRegistry.all
            .values()
            .stream()
            .map(PonderRegistry::compile)
            .flatMap(List::stream)
            .map(PonderWonderUI::new)
            ::iterator;
    }
}