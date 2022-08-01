package de.oshgnacknak.create_ponder_wonder.renderer;

import com.simibubi.create.foundation.ponder.PonderScene;
import org.lwjgl.system.MemoryUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.*;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	public static final int FPS = 60;

	private final PonderWonderUI ponder;
	private final PonderScene ponderScene;
	private final RenderUtil renderUtil;
	private int frame;

	private static final Object GL_LOCK = new Object();

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
		// if there is no more elements, throw
		if (!hasNext())
			throw new NoSuchElementException("Ponder scene is done");

		float pt = (frame % PonderRenderer.FPS) / (PonderRenderer.FPS / 3.0f);


		synchronized (GL_LOCK) {
			RenderResult res = new RenderResult(renderUtil.render(ms -> ponder.ponderWonderRenderWindow(ms, pt)), frame);
			if (frame % 3 == 2) {
				ponder.tick();
			}
			frame++;
			return res;
		}
	}

	public static class RenderResult {
		public final long image;
		public final int frame;

		public RenderResult(long image, int frame) {
			this.image = image;
			this.frame = frame;
		}

		public void writeToRawRaster(byte[] raster) {
			MemoryUtil.memByteBuffer(image, WIDTH * HEIGHT * COMPONENTS).get(0, raster);
		}
	}
}
