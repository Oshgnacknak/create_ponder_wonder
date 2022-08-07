package de.oshgnacknak.create_ponder_wonder.renderer;

import com.simibubi.create.foundation.ponder.PonderScene;
import de.oshgnacknak.create_ponder_wonder.util.AllocatedByteBuffer;
import de.oshgnacknak.create_ponder_wonder.util.ReusableObjectBuffer;
import org.lwjgl.system.MemoryUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PonderRenderer implements Iterable<PonderRenderer.RenderResult>, Iterator<PonderRenderer.RenderResult> {

	public static final int FPS = 60;
	private static final Object GL_LOCK = new Object();
	private final PonderWonderUI ponder;
	private final PonderScene ponderScene;
	private final RenderUtil renderUtil;
	private final ReusableObjectBuffer<AllocatedByteBuffer> allocatedByteBuffers;
	private int frame;

	public PonderRenderer(PonderScene ponder, ReusableObjectBuffer<AllocatedByteBuffer> allocatedByteBuffers) {
		this.ponder = new PonderWonderUI(ponder);
		ponderScene = ponder;
		this.frame = 0;
		this.renderUtil = new RenderUtil();
		this.allocatedByteBuffers = allocatedByteBuffers;
	}

	@Override
	public Iterator<RenderResult> iterator() {
		return new PonderRenderer(ponderScene, allocatedByteBuffers);
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
			RenderResult res = new RenderResult(renderUtil.render(ms -> ponder.ponderWonderRenderWindow(ms, pt), allocatedByteBuffers.get()), frame);
			if (frame % 3 == 2) {
				ponder.tick();
			}
			frame++;
			return res;
		}
	}

	public record RenderResult(AllocatedByteBuffer image, int frame) {

		public void writeToRawRaster(byte[] raster) {
			MemoryUtil.memByteBuffer(image.getAllocatedAddress(), (int) image.getSize()).get(0, raster);
		}
	}
}
