package de.oshgnacknak.create_ponder_wonder.renderer;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.util.ThreadBufferWorker;

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
		PonderRegistry.ALL.values().stream().map(PonderRegistry::compile).flatMap(List::stream).forEach(ponder -> saveFrames(ponder, basePath));

		CreatePonderWonder.LOGGER.info("All ponders rendered: {}", basePath);
		CreatePonderWonder.chat("All ponders rendered: " + basePath);
		finishRendering();
	}

	private void saveFrames(PonderScene ponder, String basePath) {
		Path videoPath = Paths.get(basePath).resolve(ponder.getId().toString().replace(":", "_") + ".mp4");
		try (ThreadBufferWorker<PonderRenderer.RenderResult> videoExporter = new ThreadVideoExporter(videoPath)) {
			for (PonderRenderer.RenderResult result : new PonderRenderer(ponder)) {
				if (!rendering) return;
				videoExporter.submitTask(result);
			}
			CreatePonderWonder.chat("Finished rendering Ponder: " + videoPath);
			CreatePonderWonder.LOGGER.info("Finished rendering Ponder: {}", videoPath);
		} catch (Exception e) {
			CreatePonderWonder.chat("Error: " + e.getMessage());
			CreatePonderWonder.LOGGER.error("Could not save ponder", e);
		}
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