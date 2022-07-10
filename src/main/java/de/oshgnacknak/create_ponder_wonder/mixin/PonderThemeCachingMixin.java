package de.oshgnacknak.create_ponder_wonder.mixin;

import com.simibubi.create.foundation.gui.Theme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Theme.class)
public abstract class PonderThemeCachingMixin {
	private static final Map<String, Integer> themeCache = new HashMap<>();

	// inject at head to catch the case where a value was already cached
	@Inject(at = @At(value = "HEAD"), method = "i(Ljava/lang/String;)I", cancellable = true, remap = false)
	private static void iHead(String key, CallbackInfoReturnable<Integer> cir) {
		// if there is a cached value, return it
		if (themeCache.containsKey(key)) {
			cir.setReturnValue(themeCache.get(key));
			cir.cancel();
		}
	}

	// inject at return to cache the value
	@Inject(at = @At(value = "RETURN"), method = "i(Ljava/lang/String;)I", remap = false)
	private static void iReturn(String key, CallbackInfoReturnable<Integer> cir) {
		themeCache.put(key, cir.getReturnValue());
	}
}
