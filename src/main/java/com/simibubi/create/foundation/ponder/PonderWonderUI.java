package com.simibubi.create.foundation.ponder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

import java.util.Collections;

public class PonderWonderUI extends PonderUI {

    public PonderWonderUI(PonderScene scene) {
        super(Collections.singletonList(scene));
        this.client = Minecraft.getInstance();
        this.textRenderer = Minecraft.getInstance().fontRenderer;
        init();
    }
    public void ponderWonderRenderWindow(MatrixStack ms, float partialTicks) {
        RenderSystem.enableBlend();
        renderVisibleScenes(ms, -10, -10, partialTicks);
    }
}
