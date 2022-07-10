package de.oshgnacknak.create_ponder_wonder.mixin;

import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import de.oshgnacknak.create_ponder_wonder.FakeDataFixer;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.datafixers.DataFixer;

@Mixin(DataFixers.class)
public class DataFixesManagerMixin {
	/**
	 * @author Grimmauld
	 * @reason trying to combat weird memory leaks
	 */
	@Overwrite
	private static DataFixer createFixerUpper() {
		CreatePonderWonder.LOGGER.info("Removing DataFixer");
		return new FakeDataFixer();
	}
}
