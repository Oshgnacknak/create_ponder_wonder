package de.oshgnacknak.create_ponder_wonder.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.ui.PonderUI;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class PonderWonderUI {
	private static final Method renderVisibleScenes;
	private static final Method renderWidgets;
	private static final Constructor<PonderUI> ponderUIConstructor;

	static {
		try {
			renderVisibleScenes = PonderUI.class.getDeclaredMethod("renderVisibleScenes", PoseStack.class, int.class, int.class, float.class);
			renderWidgets = PonderUI.class.getDeclaredMethod("renderWidgets", PoseStack.class, int.class, int.class, float.class);
			ponderUIConstructor = PonderUI.class.getDeclaredConstructor(List.class);

			// set stuff accessible
			renderVisibleScenes.setAccessible(true);
			renderWidgets.setAccessible(true);
			ponderUIConstructor.setAccessible(true);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private final PonderUI ui;


	public PonderWonderUI(PonderScene scene) {
		List<PonderScene> uis = Collections.singletonList(scene);

		try {
			ui = ponderUIConstructor.newInstance(uis);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		ui.init(Minecraft.getInstance(), (int) (RenderUtil.WIDTH / RenderUtil.SCALE), (int) (RenderUtil.HEIGHT / RenderUtil.SCALE));
	}

	public void ponderWonderRenderWindow(PoseStack ms, float partialTicks) {
		try {
			ms.pushPose();
			// scale in the corner
			renderVisibleScenes.invoke(ui, ms, -10, -10, partialTicks);
			renderWidgets.invoke(ui, ms, -10, -10, partialTicks);
			ms.popPose();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void tick() {
		ui.tick();
	}

	public boolean isFinished() {
		return ui.getActiveScene().isFinished();
	}
}
