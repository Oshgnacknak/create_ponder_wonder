package de.oshgnacknak.create_ponder_wonder.renderer;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.util.AllocatedByteBuffer;
import de.oshgnacknak.create_ponder_wonder.util.ReusableObjectBuffer;
import de.oshgnacknak.create_ponder_wonder.util.ThreadBufferWorker;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static de.oshgnacknak.create_ponder_wonder.renderer.RenderUtil.*;

public class ThreadVideoExporter extends ThreadBufferWorker<PonderRenderer.RenderResult> {
	private final IMediaWriter writer;
	private final long startTime;
	private final ReusableObjectBuffer<BufferedImage> imageBuffer;
	private int frames = 0;
	private final ReusableObjectBuffer<AllocatedByteBuffer> bytebuffers;


	public ThreadVideoExporter(Path pathToVideo) throws IOException {
		super();
		this.bytebuffers = new ReusableObjectBuffer<>(11, () -> new AllocatedByteBuffer(BYTE_SIZE));
		startTime = System.currentTimeMillis();
		Files.createDirectories(pathToVideo.getParent());
		writer = ToolFactory.makeWriter(pathToVideo.toString());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(PonderRenderer.FPS), WIDTH, HEIGHT);
		imageBuffer = new ReusableObjectBuffer<>(getThreadSize(), () -> new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR));
	}

	public ReusableObjectBuffer<AllocatedByteBuffer> getBytebuffers() {
		return bytebuffers;
	}

	@Override
	public void runTask(PonderRenderer.RenderResult renderResult) {
		frames++;

		BufferedImage image = imageBuffer.get();
		renderResult.writeToRawRaster(((DataBufferByte) image.getRaster().getDataBuffer()).getData());
		bytebuffers.put(renderResult.image);

		synchronized (writer) {
			writer.encodeVideo(0, image, renderResult.frame * 1000000L / PonderRenderer.FPS, TimeUnit.MICROSECONDS);
		}

		image.flush();
		imageBuffer.put(image);
	}

	@Override
	public void close() {
		super.close();
		writer.close();
		// FPS
		CreatePonderWonder.LOGGER.info("FPS: {}", (frames * 1000L / (System.currentTimeMillis() - startTime)));
		imageBuffer.clear();
		// ImgurUploader.tryUpload(writer.getUrl());
	}
}
