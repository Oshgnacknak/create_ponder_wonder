package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWonderUI;
import net.minecraft.client.renderer.texture.NativeImage;

import java.util.Iterator;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	private static final int FPS = 60;
	private static final int MAX_FRAMES = Integer.MAX_VALUE; // FPS*3;
	private final PonderWonderUI ponder;
	private int frame;
	public PonderRenderer(PonderScene ponder) {
		this.ponder = new PonderWonderUI(ponder);
		this.frame = -1;
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
		float pt = (frame % PonderRenderer.FPS) / (PonderRenderer.FPS / 3.0f);
		NativeImage img = RenderUtils.render(ms ->
			ponder.ponderWonderRenderWindow(ms, pt));

		if (frame % 3 == 2) {
			ponder.tick();
		}
		frame++;

		return new RenderResult(img, frame);
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
