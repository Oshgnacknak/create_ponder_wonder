package de.oshgnacknak.create_ponder_wonder.mixin;

import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class PauseClientMixin {

	// @Inject(at = @At(value = "INVOKE"), method = "Lnet/minecraft/client/Minecraft;runTick()V", cancellable = true)
	private void tick(CallbackInfo ci) {
		if (CreatePonderWonder.PONDER_RENDERER.isRendering())
			ci.cancel();
	}
}
