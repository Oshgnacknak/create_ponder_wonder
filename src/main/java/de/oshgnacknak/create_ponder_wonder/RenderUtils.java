package de.oshgnacknak.create_ponder_wonder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class RenderUtils {

    private static final double SCALE = 1;
    private static final int WIDTH = 720;
    private static final int HEIGHT = 480;
    private static final double Z_DISTANCE = 1500;
    private static final boolean INCLUDE_FRAME = false;

    private RenderUtils() {}

    public static NativeImage render(Consumer<MatrixStack> renderFunc) {
        int realWidth = scale(WIDTH);
        int realHeight = scale(HEIGHT);
        testForValidSize(realWidth, realHeight);


        Framebuffer fb = new Framebuffer(realWidth, realHeight, true, Minecraft.IS_RUNNING_ON_MAC);

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.clear(16640, Minecraft.IS_RUNNING_ON_MAC);
        fb.bindFramebuffer(true);
        FogRenderer.setFogBlack();

        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        RenderSystem.viewport(0, 0, realWidth, realHeight);

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, WIDTH, HEIGHT, 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.loadIdentity();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(
            WIDTH / 2.0,
            HEIGHT / 2.0,
            -Z_DISTANCE);
        net.minecraft.client.renderer.RenderHelper.enableGuiDepthLighting();

        RenderSystem.defaultAlphaFunc();

        renderFunc.accept(matrixStack);

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();

        NativeImage img = ScreenShotHelper.createScreenshot(realWidth, realHeight, fb);

        if (INCLUDE_FRAME) {
            applyFrame(img);
        }

        return img;
    }

    private static void testForValidSize(int realWidth, int realHeight) {
        int maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);

        if (realWidth > maxTextureSize || realHeight > maxTextureSize) {
            throw new IllegalStateException(String.format(
                "Image would be to large: %dx%d",
                realWidth,
                realHeight));
        }
    }

    private static void applyFrame(NativeImage img) {
        img.fillAreaRGBA(scale(0), scale(0), scale(2), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(0), scale(1), scale(1), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(WIDTH - 2), scale(0), scale(2), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(WIDTH - 1), scale(1), scale(1), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(0), scale(HEIGHT - 1), scale(2), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(0), scale(HEIGHT - 2), scale(1), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(WIDTH - 2), scale(HEIGHT - 1), scale(2), scale(1), 0x00000000);
        img.fillAreaRGBA(scale(WIDTH - 1), scale(HEIGHT - 2), scale(1), scale(1), 0x00000000);

        img.fillAreaRGBA(scale(1), scale(1), scale(1), scale(1), 0xFF999999);
        img.fillAreaRGBA(scale(WIDTH - 2), scale(1), scale(1), scale(1), 0xFF999999);
        img.fillAreaRGBA(scale(1), scale(HEIGHT - 2), scale(1), scale(1), 0xFF999999);
        img.fillAreaRGBA(scale(WIDTH - 2), scale(HEIGHT - 2), scale(1), scale(1), 0xFF999999);

        img.fillAreaRGBA(scale(2), scale(0), scale(WIDTH - 4), scale(1), 0xFF999999);
        img.fillAreaRGBA(scale(2), scale(HEIGHT - 1), scale(WIDTH - 4), scale(1), 0xFF999999);
        img.fillAreaRGBA(scale(0), scale(2), scale(1), scale(HEIGHT - 4), 0xFF999999);
        img.fillAreaRGBA(scale(WIDTH - 1), scale(2), scale(1), scale(HEIGHT - 4), 0xFF999999);

        img.fillAreaRGBA(scale(2), scale(1), scale(WIDTH - 4), scale(1), 0xFFD8D8D8);
        img.fillAreaRGBA(scale(2), scale(HEIGHT - 2), scale(WIDTH - 4), scale(1), 0xFFB3B3B3);
        img.fillAreaRGBA(scale(1), scale(2), scale(1), scale(HEIGHT - 4), 0xFFD8D8D8);
        img.fillAreaRGBA(scale(WIDTH - 2), scale(2), scale(1), scale(HEIGHT - 4), 0xFFB3B3B3);
    }

    private static int scale(int value) {
        return (int) Math.round(value * SCALE);
    }
}
