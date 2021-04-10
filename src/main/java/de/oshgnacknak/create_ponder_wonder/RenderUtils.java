package de.oshgnacknak.create_ponder_wonder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class RenderUtils {

    private static final double SCALE = 5;
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final double Z_DISTANCE = 1400;

    private RenderUtils() {}

    public static NativeImage render(Consumer<MatrixStack> renderFunc) {
        Framebuffer fb = new Framebuffer(WIDTH, HEIGHT, true, Minecraft.IS_RUNNING_ON_MAC);

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.clear(16640, Minecraft.IS_RUNNING_ON_MAC);
        fb.bindFramebuffer(true);
        FogRenderer.setFogBlack();

        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        RenderSystem.viewport(0, 0, WIDTH, HEIGHT);

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0d,
            WIDTH / SCALE,
            HEIGHT / SCALE,
            0.0d, 0,
            3000.0d);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.loadIdentity();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(
            WIDTH / (SCALE * 2.0),
            HEIGHT / (SCALE * 2.0),
            -Z_DISTANCE);
        net.minecraft.client.renderer.RenderHelper.enableGuiDepthLighting();

        RenderSystem.defaultAlphaFunc();

        renderFunc.accept(matrixStack);

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();

        return ScreenShotHelper.createScreenshot(WIDTH, HEIGHT, fb);
    }

}
