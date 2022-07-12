package de.oshgnacknak.create_ponder_wonder.renderer;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import de.oshgnacknak.create_ponder_wonder.util.ImgurUploader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.HEIGHT;

public class ThreadVideoExporter implements AutoCloseable {
	private final IMediaWriter writer;
	private final ThreadPoolExecutor executor;

	public ThreadVideoExporter(Path pathToVideo) throws IOException {
		// first create folder structure
		Files.createDirectories(pathToVideo.getParent());

		// then create the video
		writer = ToolFactory.makeWriter(pathToVideo.toString());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS), RenderUtil.WIDTH, HEIGHT);
		executor = new ScheduledThreadPoolExecutor(11);
	}

	public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
		BufferedImage image;
		// if the source image is already the target type, return the source image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new image

		else {
			image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
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
		if (result.image == null) return;
		BufferedImage image = new BufferedImage(RenderUtil.WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		if (result.image.format().hasAlpha()) {
			for (int x = 0; x < RenderUtil.WIDTH; ++x) {
				for (int y = 0; y < HEIGHT; ++y) {
					int i = result.image.getPixelRGBA(x, y);
					int r = (i & 0xff) << 16;
					int g = i & 0xff00;
					int b = (i & 0xff0000) >> 16;
					image.setRGB(x, HEIGHT - y - 1, r + b + g);
				}
			}
		}

		result.image.close();

		synchronized (writer) {
			writer.encodeVideo(0, image, (long) result.frame * 1000000000 / PonderRenderer.FPS, TimeUnit.NANOSECONDS);
		}
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
