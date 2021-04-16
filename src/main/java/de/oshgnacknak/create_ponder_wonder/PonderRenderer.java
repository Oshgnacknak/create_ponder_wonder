package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWonderUI;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

    public static class RenderResult {
        public final NativeImage image;
        public final int frame;

        public RenderResult(NativeImage image, int frame) {
            this.image = image;
            this.frame = frame;
        }
    }

    private static final int FPS = 60;
    private static final int MAX_FRAMES = FPS*3;

    private final PonderWonderUI ponder;
    private int frame;

    public PonderRenderer(PonderScene ponder) {
        this.ponder = new PonderWonderUI(ponder);
        this.frame = 0;
    }

    @Override
    public Iterator<RenderResult> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return !ponder.isFinished() && frame < MAX_FRAMES;
    }

    @Override
    public RenderResult next() {
        try {
            Promise<NativeImage> promise = GlobalEventExecutor.INSTANCE.newPromise();

            float pt = (frame % PonderRenderer.FPS) / (PonderRenderer.FPS / 3.0f);
            Minecraft.getInstance().field_213275_aU.add(() -> {
                try {
                    NativeImage img = RenderUtils.render(ms ->
                            ponder.ponderWonderRenderWindow(ms, pt));
                    promise.setSuccess(img);
                } catch (Exception e) {
                    promise.setFailure(e);
                }
            });

            RenderResult res =  new RenderResult(promise.get(), frame);

            if (frame % 3 == 2) {
                ponder.tick();
            }
            frame++;

            return res;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
