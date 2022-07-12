package de.oshgnacknak.create_ponder_wonder;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadVideoExporter implements AutoCloseable {
	private final IMediaWriter writer;
	private final ScheduledThreadPoolExecutor executor;

	public ThreadVideoExporter(Path pathToVideo) throws IOException {
		// first create folder structure
		Files.createDirectories(pathToVideo.getParent());

		// then create the video
		writer = ToolFactory.makeWriter(pathToVideo.toString());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS), RenderUtil.WIDTH, RenderUtil.HEIGHT);
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
		try (SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel(190000)) {
			result.image.writeToChannel(channel);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(channel.array(), 0, (int) channel.size()));
			if (image == null) return;
			image = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
			synchronized (writer) {
				writer.encodeVideo(0, image, (long) result.frame * 1000000000 / PonderRenderer.FPS, TimeUnit.NANOSECONDS);
			}
		} catch (IOException e) {
			CreatePonderWonder.LOGGER.error("Error writing image to channel", e);
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
		System.gc();
	}
}
