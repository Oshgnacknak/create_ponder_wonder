package de.oshgnacknak.create_ponder_wonder.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.mojang.blaze3d.platform.GlStateManager.setupLevelDiffuseLighting;

@Mixin(GlStateManager.class)
public class GlStateManagerMatrixCacheMixin {
	private static final Matrix4f matrix4fCache = new Matrix4f();

	static {
		matrix4fCache.setIdentity();
		matrix4fCache.multiply(Vector3f.YP.rotationDegrees(62.0F));
		matrix4fCache.multiply(Vector3f.XP.rotationDegrees(185.5F));
		matrix4fCache.multiply(Vector3f.YP.rotationDegrees(-22.5F));
		matrix4fCache.multiply(Vector3f.XP.rotationDegrees(135.0F));
	}

	/**
	 * @author Grimmauld
	 * @reason cache the matrix for the gi state manager
	 */
	@Overwrite
	public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f1) {
		setupLevelDiffuseLighting(vector3f, vector3f1, matrix4fCache);
	}
}
