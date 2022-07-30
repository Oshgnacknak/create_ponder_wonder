package de.oshgnacknak.create_ponder_wonder.renderer;

import com.simibubi.create.foundation.ponder.PonderScene;
import org.lwjgl.system.MemoryUtil;

import java.util.Iterator;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.*;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	public static final int FPS = 60;

	private final PonderWonderUI ponder;
	private final PonderScene ponderScene;
	private final RenderUtil renderUtil;
	private int frame;

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
		public final long image;
		public final int frame;

		public RenderResult(long image, int frame) {
			this.image = image;
			this.frame = frame;
		}

		public void writeToRaster(byte[] bytes) {
			for (int x = 0; x < WIDTH; ++x) {
				for (int y = 0; y < HEIGHT; ++y) {
					int i = MemoryUtil.memGetInt(image + (x + (long) y * WIDTH) * COMPONENTS);
					int baseAddress = (x + (HEIGHT - y - 1) * WIDTH) * COMPONENTS; // flip y: y = HEIGHT - y - 1
					bytes[baseAddress + 2] = (byte) (i & 0xFF);
					bytes[baseAddress + 1] = (byte) ((i & 0xFF00) >> 8);
					bytes[baseAddress] = (byte) ((i & 0xFF0000) >> 16);
				}
			}
		}
	}
}
