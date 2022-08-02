package de.oshgnacknak.create_ponder_wonder.renderer;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.util.ThreadBufferWorker;
import de.oshgnacknak.create_ponder_wonder.util.ThreadableTask;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.HEIGHT;
import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.WIDTH;

public class ThreadVideoExporter extends ThreadBufferWorker<PonderRenderer.RenderResult> {
	private final IMediaWriter writer;

	private int frames = 0;
	private final long startTime;

	public ThreadVideoExporter(Path pathToVideo) throws IOException {
		super();
		startTime = System.currentTimeMillis();
		Files.createDirectories(pathToVideo.getParent());
		writer = ToolFactory.makeWriter(pathToVideo.toString());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS), WIDTH, HEIGHT);
	}

	@Override
	public void runTask(PonderRenderer.RenderResult renderResult) {
		frames++;

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		renderResult.writeToRawRaster(((DataBufferByte) image.getRaster().getDataBuffer()).getData());

		synchronized (writer) {
			writer.encodeVideo(0, image, renderResult.frame * 1000000000L / PonderRenderer.FPS, TimeUnit.NANOSECONDS);
		}

		image.flush();
		MemoryUtil.nmemFree(renderResult.image);
	}

	@Override
	public void close() {
		super.close();
		writer.close();
		// FPS
		CreatePonderWonder.LOGGER.info("FPS: {}", (frames * 1000L / (System.currentTimeMillis() - startTime)));
		// ImgurUploader.tryUpload(writer.getUrl());
	}
}
