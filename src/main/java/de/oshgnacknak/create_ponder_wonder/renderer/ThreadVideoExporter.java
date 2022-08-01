package de.oshgnacknak.create_ponder_wonder.renderer;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import de.oshgnacknak.create_ponder_wonder.util.ImgurUploader;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.HEIGHT;
import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.WIDTH;

public class ThreadVideoExporter implements AutoCloseable {
	private final IMediaWriter writer;
	private final ThreadPoolExecutor executor;

	public ThreadVideoExporter(Path pathToVideo) throws IOException {
		// first create folder structure
		Files.createDirectories(pathToVideo.getParent());

		// then create the video
		writer = ToolFactory.makeWriter(pathToVideo.toString());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS), WIDTH, HEIGHT);
		executor = new ScheduledThreadPoolExecutor(11);
	}

	public void addFrame(PonderRenderer.RenderResult result) {
		// fixme
		while (executor.getActiveCount() > 10) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executor.submit(() -> encodeFrameToVideo(result));
	}


	private void encodeFrameToVideo(PonderRenderer.RenderResult result) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		result.writeToRawRaster(((DataBufferByte) image.getRaster().getDataBuffer()).getData());

		synchronized (writer) {
			writer.encodeVideo(0, image, (long) result.frame * 1000000000 / PonderRenderer.FPS, TimeUnit.NANOSECONDS);
		}

		image.flush();
		MemoryUtil.nmemFree(result.image);
	}

	@Override
	public void close() {
		// Fixme
		while (executor.getActiveCount() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		writer.close();
		ImgurUploader.tryUpload(writer.getUrl());
	}
}
