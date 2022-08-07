package de.oshgnacknak.create_ponder_wonder.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.util.AllocatedByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;

public class RenderUtil {

	public static final float SCALE = 3;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final int COMPONENTS = 3;
	public static final long BYTE_SIZE = WIDTH * (long) HEIGHT * COMPONENTS;
	public static final int PIXEL_FORMAT = GL12.GL_BGR;
	private static final Matrix4f viewField = Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, 0, 10000);
	private static final Lazy<TextureTarget> lazyRenderTarget = Lazy.of(() -> {
		TextureTarget renderTarget = new TextureTarget(WIDTH, HEIGHT, true, Minecraft.ON_OSX);
		renderTarget.setClearColor(0, 0, 0, 0);
		return renderTarget;
	});

	public static void downloadToBuffer(RenderTarget renderTarget, AllocatedByteBuffer buffer) {
		buffer.assertSize(BYTE_SIZE);
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glBindTexture(GL_TEXTURE_2D, renderTarget.getColorTextureId());
		GL11.glGetTexImage(GL_TEXTURE_2D, 0, PIXEL_FORMAT, GL_UNSIGNED_BYTE, buffer.getAllocatedAddress());
	}

	public AllocatedByteBuffer render(Consumer<PoseStack> renderFunc, AllocatedByteBuffer buffer) {
		lazyRenderTarget.get().clear(true);
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();

		RenderSystem.enableBlend();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		lazyRenderTarget.get().bindWrite(true);
		FogRenderer.setupNoFog();

		RenderSystem.enableTexture();
		RenderSystem.enableCull();

		RenderSystem.viewport(0, 0, WIDTH, HEIGHT);
		modelViewStack.setIdentity();
		modelViewStack.translate(0, HEIGHT, -5000);
		modelViewStack.scale(1, -1, 1);

		RenderSystem.applyModelViewMatrix();
		RenderSystem.setProjectionMatrix(viewField);

		Lighting.setupFor3DItems();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		PoseStack ponderPoseStack = new PoseStack();
		ponderPoseStack.scale(SCALE, SCALE, SCALE);
		try {
			renderFunc.accept(ponderPoseStack);
		} catch (RuntimeException e) {
			CreatePonderWonder.LOGGER.error("Could not print ponder: {}", e.getMessage());
		}

		RenderSystem.disableBlend();
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
		downloadToBuffer(lazyRenderTarget.get(), buffer);
		return buffer;
	}
}
