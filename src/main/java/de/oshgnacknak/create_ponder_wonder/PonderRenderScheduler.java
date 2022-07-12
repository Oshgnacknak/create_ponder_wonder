package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IRational;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PonderRenderScheduler {
	private boolean rendering;

	public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
		BufferedImage image;
		// if the source image is already the target type, return the source image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new image

		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}

	public void start(String basePath) {
		if (rendering) {
			CreatePonderWonder.chat("Error: cannot be done twice");
			throw new IllegalStateException("Cannot start rendering twice");
		}

		rendering = true;

		CreatePonderWonder.LOGGER.info("Started rendering ponders");
		CreatePonderWonder.chat("Started rendering ponders");

		renderAllPonders(basePath);
	}

	private void renderAllPonders(String basePath) {
		PonderRegistry.ALL.values().stream().map(PonderRegistry::compile).flatMap(List::stream).forEach(ponder -> saveFrames(ponder, basePath));

		CreatePonderWonder.LOGGER.info("All ponders rendered: {}", basePath);
		CreatePonderWonder.chat("All ponders rendered: " + basePath);
		finishRendering();
	}

	private void saveFrames(PonderScene ponder, String basePath) {
		try {
			Path path = getOutPath(ponder, basePath);
			final IMediaWriter writer = ToolFactory.makeWriter(path.resolve(ponder.getId().toString().replace(":", "_") + ".mp4").toString());

			// We tell it we're going to add one video stream, with id 0,
			// at position 0, and that it will have a fixed frame rate of FRAME_RATE.
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS),  RenderUtil.WIDTH, RenderUtil.HEIGHT);


			for (PonderRenderer.RenderResult result : new PonderRenderer(ponder)) {
				if (!rendering) return;
				if (result.image == null) continue;
				SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel(180000); // roughly 180 kb per image
				result.image.writeToChannel(channel);
				BufferedImage image = convertToType(ImageIO.read(new ByteArrayInputStream(channel.array(), 0, (int) channel.size())), BufferedImage.TYPE_3BYTE_BGR);
				writer.encodeVideo(0, image, (long) result.frame * 1000000000 / PonderRenderer.FPS, TimeUnit.NANOSECONDS);
				// print frame number
				System.out.println("Frame " + result.frame);
				// result.image.writeToFile(path.resolve(String.format("%06d.png", result.frame)));
			}
			writer.close();
			System.gc();

			CreatePonderWonder.chat("Finished rendering Ponder: " + path);
			CreatePonderWonder.LOGGER.info("Finished rendering Ponder: {}", path);
		} catch (Exception e) {
			CreatePonderWonder.chat("Error: " + e.getMessage());
			CreatePonderWonder.LOGGER.error("Could not save image", e);
			e.printStackTrace();
		}
	}

	private Path getOutPath(PonderScene ponder, String basePath) throws IOException {
		return Files.createDirectories(Paths.get(basePath, CreatePonderWonder.MODID, ponder.getString("out")));
	}

	public void stop() {
		if (!rendering) {
			CreatePonderWonder.chat("Aleady stopped...");
			return;
		}

		CreatePonderWonder.LOGGER.warn("Stopping rendering ponders abruptly");
		CreatePonderWonder.chat("Stopping rendering ponders abruptly");
		finishRendering();
	}

	private void finishRendering() {
		rendering = false;
		System.gc();

		CreatePonderWonder.LOGGER.info("Stopped rendering ponders");
		CreatePonderWonder.chat("Stopped rendering ponders");
	}

	public boolean isRendering() {
		return rendering;
	}
}