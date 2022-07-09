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

	private static final int FPS = 60;
	private static final int MAX_FRAMES = Integer.MAX_VALUE;

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
		Promise<RenderResult> promise = GlobalEventExecutor.INSTANCE.newPromise();

		Minecraft.getInstance().progressTasks.add(() -> {
		    try {
				float pt = (frame % PonderRenderer.FPS) / (PonderRenderer.FPS / 3.0f);
				NativeImage img = RenderUtils.render(ms ->
					ponder.ponderWonderRenderWindow(ms, pt));
				promise.setSuccess(new RenderResult(img, frame));

				if (frame % 3 == 2) {
					ponder.tick();
				}
				frame++;
			} catch (Throwable e) {
				promise.setFailure(e);
			}
		});

		try {
			return promise.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static class RenderResult {
		public final NativeImage image;
		public final int frame;

		public RenderResult(NativeImage image, int frame) {
			this.image = image;
			this.frame = frame;
		}
	}
}
