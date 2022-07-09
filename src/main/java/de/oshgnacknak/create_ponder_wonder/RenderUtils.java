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

    public static final double SCALE = 5;
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final double Z_DISTANCE = 1400;

    private RenderUtils() {}

    public static NativeImage render(Consumer<MatrixStack> renderFunc) {
        Framebuffer fb = new Framebuffer(WIDTH, HEIGHT, true, Minecraft.ON_OSX);

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        fb.bindWrite(true);
        FogRenderer.setupNoFog();

        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        RenderSystem.viewport(0, 0, WIDTH, HEIGHT);

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(
            0.0d,
            WIDTH,
            HEIGHT,
            0.0d, 0,
            3000.0d);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.loadIdentity();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0, 0, -Z_DISTANCE);
        net.minecraft.client.renderer.RenderHelper.setupFor3DItems();

        RenderSystem.defaultAlphaFunc();

        renderFunc.accept(matrixStack);

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();

        return ScreenShotHelper.takeScreenshot(WIDTH, HEIGHT, fb);
    }

}
