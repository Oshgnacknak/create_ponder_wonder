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

public class RenderUtil {

	public static final float SCALE = 3;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	private final RenderTarget renderTarget;
	private final PoseStack ponderPoseStack;
	private final Matrix4f viewField;

	public RenderUtil() {
		renderTarget = new TextureTarget(WIDTH, HEIGHT, true, Minecraft.ON_OSX);
		renderTarget.setClearColor(0, 0, 0, 0);
		ponderPoseStack = new PoseStack();
		ponderPoseStack.scale(SCALE, SCALE, SCALE);
		viewField = Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, 0, 10000);
	}

	public NativeImage render(Consumer<PoseStack> renderFunc) {
		renderTarget.clear(true);
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();

		RenderSystem.enableBlend();
		RenderSystem.clear(0x4100, Minecraft.ON_OSX);
		renderTarget.bindWrite(true);
		FogRenderer.setupNoFog();

		RenderSystem.enableTexture();
		RenderSystem.enableCull();

		RenderSystem.viewport(0, 0, WIDTH, HEIGHT);
		modelViewStack.setIdentity();
		modelViewStack.translate(0, 0, -5000);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setProjectionMatrix(viewField);

		Lighting.setupFor3DItems();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		try {
			renderFunc.accept(ponderPoseStack);
		} catch (RuntimeException e) {
			CreatePonderWonder.LOGGER.error("Could not print ponder: {}", e.getMessage());
		}

		RenderSystem.disableBlend();
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
		return Screenshot.takeScreenshot(renderTarget);
	}
}
