package de.oshgnacknak.create_ponder_wonder.mixin;

import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class PauseServerMixin {

	@Inject(at = @At(value = "INVOKE"), method = "Lnet/minecraft/server/MinecraftServer;updateTimeLightAndEntities(Ljava/util/function/BooleanSupplier;)V", cancellable = true)
	private void tick(BooleanSupplier sup, CallbackInfo ci) {
		if (CreatePonderWonder.PONDER_RENDERER.isRendering())
			ci.cancel();
	}
}
