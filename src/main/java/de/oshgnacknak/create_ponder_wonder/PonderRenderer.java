package de.oshgnacknak.create_ponder_wonder;

import com.mojang.blaze3d.platform.NativeImage;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.util.Iterator;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	private static final int FPS = 60;

	private final PonderWonderUI ponder;
	private final PonderScene ponderScene;
	private int frame;
	private final RenderUtil renderUtil;

	public PonderRenderer(PonderScene ponder) {
		this.ponder = new PonderWonderUI(ponder);
		ponderScene = ponder;
		this.frame = 0;
		this.renderUtil = new RenderUtil();
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
		NativeImage img = renderUtil.render(ms ->
				ponder.ponderWonderRenderWindow(ms, pt));
		RenderResult res = new RenderResult(img, frame);

		if (frame % 3 == 2) {
			ponder.tick();
		}
		frame++;
		return res;
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
