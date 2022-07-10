package de.oshgnacknak.create_ponder_wonder;

import com.mojang.blaze3d.platform.NativeImage;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.util.Iterator;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	private static final int FPS = 60;

	private final PonderWonderUI ponder;
	private final PonderScene ponderScene;
	private int frame;

	public PonderRenderer(PonderScene ponder) {
		this.ponder = new PonderWonderUI(ponder);
		ponderScene = ponder;
		this.frame = 0;
	}

	@Override
	public Iterator<RenderResult> iterator() {
		return new PonderRenderer(ponderScene);
	}

	@Override
	public boolean hasNext() {
		return !ponder.isFinished();
	}

	@Override
	public RenderResult next() {
		float pt = (frame % PonderRenderer.FPS) / (PonderRenderer.FPS / 3.0f);
		NativeImage img = RenderUtils.render(ms ->
				ponder.ponderWonderRenderWindow(ms, pt));
		RenderResult res = new RenderResult(img, frame);

		if (frame % 3 == 2) {
			ponder.tick();
		}
		frame++;
		return res;

		/*

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
		 */
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
