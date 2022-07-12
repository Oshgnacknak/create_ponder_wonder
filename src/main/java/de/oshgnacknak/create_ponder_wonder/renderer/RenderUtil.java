package de.oshgnacknak.create_ponder_wonder.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Consumer;

public class RenderUtil {

	public static final float SCALE = 3;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	private static final Matrix4f viewField = Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, 0, 10000);
	private static final Lazy<TextureTarget> lazyRenderTarget = Lazy.of(() -> {
		TextureTarget renderTarget = new TextureTarget(WIDTH, HEIGHT, true, Minecraft.ON_OSX);
		renderTarget.setClearColor(0, 0, 0, 0);
		return renderTarget;
	});

	public RenderUtil() {
	}

	public static NativeImage downloadToBuffer(RenderTarget renderTarget) {
		NativeImage nativeimage = new NativeImage(WIDTH, HEIGHT, false);
		nativeimage.format().setPackPixelStoreState();
		RenderSystem.bindTexture(renderTarget.getColorTextureId());
		GlStateManager._getTexImage(3553, 0, nativeimage.format().glFormat(), 5121, nativeimage.pixels);
		return nativeimage;

		/*
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		if (nativeimage.format().hasAlpha()) {
			for (int x = 0; x < WIDTH; ++x) {
				for (int y = 0; y < HEIGHT; ++y) {
					int i = nativeimage.getPixelRGBA(x, y);
					int r = (i & 0xff) << 16;
					int g = i & 0xff00;
					int b = (i & 0xff0000) >> 16;
					image.setRGB(x, HEIGHT - y - 1, r + b + g);
				}
			}
		}

		nativeimage.close();
		return image;
		 */
	}

	public NativeImage render(Consumer<PoseStack> renderFunc) {
		lazyRenderTarget.get().clear(true);
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();

		RenderSystem.enableBlend();
		RenderSystem.clear(0x4100, Minecraft.ON_OSX);
		lazyRenderTarget.get().bindWrite(true);
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
		return downloadToBuffer(lazyRenderTarget.get());
	}
}
