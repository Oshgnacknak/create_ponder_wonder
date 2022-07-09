package com.simibubi.create.foundation.ponder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

import java.util.Collections;

public class PonderWonderUI extends PonderUI {

    public PonderWonderUI(PonderScene scene) {
        super(Collections.singletonList(scene));
        this.minecraft = Minecraft.getInstance();
        this.font = Minecraft.getInstance().font;
        init();
    }

    public void ponderWonderRenderWindow(MatrixStack ms, float partialTicks) {
        RenderSystem.enableBlend();
        renderVisibleScenes(ms, -10, -10, partialTicks);
        renderWidgets(ms, -10, -10, partialTicks);
    }

    public boolean isFinished() {
        return getActiveScene().finished;
    }
}
