package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderScene;

import java.awt.image.BufferedImage;
import java.util.Iterator;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	public static final int FPS = 60;

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
		RenderResult res = new RenderResult(renderUtil.render(ms ->
				ponder.ponderWonderRenderWindow(ms, pt)), frame);

		if (frame % 3 == 2) {
			ponder.tick();
		}
		frame++;
		return res;
	}

	public static class RenderResult {
		public final BufferedImage image;
		public final int frame;

		public RenderResult(BufferedImage image, int frame) {
			this.image = image;
			this.frame = frame;
		}
	}
}
