package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PonderRenderScheduler {
	private boolean rendering;

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
		PonderRegistry.ALL
				.values()
				.stream()
				.map(PonderRegistry::compile)
				.flatMap(List::stream)
				.forEach(ponder -> saveFrames(ponder, basePath));

		CreatePonderWonder.LOGGER.info("All ponders rendered: {}", basePath);
		CreatePonderWonder.chat("All ponders rendered: " + basePath);
		finishRendering();
	}

	private void saveFrames(PonderScene ponder, String basePath) {
		try {
			Path path = getOutPath(ponder, basePath);

			for (PonderRenderer.RenderResult result : new PonderRenderer(ponder)) {
				if (!rendering)
					return;
				result.image.writeToFile(path.resolve(String.format("%06d.png", result.frame)));
			}
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
		return Files.createDirectories(Paths.get(
				basePath,
				CreatePonderWonder.MODID,
				ponder.getString("out")));
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