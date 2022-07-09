package de.oshgnacknak.create_ponder_wonder;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.FogRenderer;

import java.util.function.Consumer;

public class RenderUtils {

	public static final float SCALE = 5;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final double Z_DISTANCE = 2000;

	private RenderUtils() {
	}

	public static NativeImage render(Consumer<PoseStack> renderFunc) {
		int realWidth = Math.round(1 * WIDTH);
		int realHeight = Math.round(1 * HEIGHT);

		RenderTarget fb = new TextureTarget(realWidth, realHeight, true, Minecraft.ON_OSX);
		fb.setClearColor(0, 0, 0, 0);
		fb.clear(true);

		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();

		RenderSystem.enableBlend();
		RenderSystem.clear(0x4100, Minecraft.ON_OSX);
		fb.bindWrite(true);
		FogRenderer.setupNoFog();

		RenderSystem.enableTexture();
		RenderSystem.enableCull();

		RenderSystem.viewport(0, 0, realWidth, realHeight);

		modelViewStack.setIdentity();
		modelViewStack.translate(0, 0, -Z_DISTANCE);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setProjectionMatrix(Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, 1000, 3000));

		PoseStack poseStack = new PoseStack();
		Lighting.setupFor3DItems();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		try {
			renderFunc.accept(poseStack);
		} catch (RuntimeException e) {
			CreatePonderWonder.LOGGER.error("Could not print ponder: {}", e.getMessage());
		}

		RenderSystem.disableBlend();
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();

		return Screenshot.takeScreenshot(fb);
	}

	// See Screenshot.takeScreenshot
	private static NativeImage takeNonOpaqueScreenshot(RenderTarget fb) {
		NativeImage img = new NativeImage(fb.width, fb.height, false);
		RenderSystem.bindTexture(fb.getColorTextureId());
		img.downloadTexture(0, false);
		img.flipY();
		return img;
	}
}
