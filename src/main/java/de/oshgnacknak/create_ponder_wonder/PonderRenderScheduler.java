package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
		Path videoPath;
		try {
			videoPath = getOutPath(ponder, basePath).resolve(ponder.getId().toString().replace(":", "_") + ".mp4");
		} catch (IOException e) {
			CreatePonderWonder.LOGGER.error("Error creating video path", e);
			return;
		}
		try (ThreadVideoExporter videoExporter = new ThreadVideoExporter(videoPath)) {
			for (PonderRenderer.RenderResult result : new PonderRenderer(ponder)) {
				if (!rendering) return;
				videoExporter.addFrame(result);
				System.out.println("Frame " + result.frame);
			}
			CreatePonderWonder.chat("Finished rendering Ponder: " + videoPath);
			CreatePonderWonder.LOGGER.info("Finished rendering Ponder: {}", videoPath);
		} catch (Exception e) {
			CreatePonderWonder.chat("Error: " + e.getMessage());
			CreatePonderWonder.LOGGER.error("Could not save ponder", e);
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