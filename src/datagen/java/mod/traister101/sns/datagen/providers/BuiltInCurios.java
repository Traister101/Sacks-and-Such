package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;
import top.theillusivec4.curios.api.CuriosDataProvider;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.inventory.InventoryMenu;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BuiltInCurios extends CuriosDataProvider {

	public BuiltInCurios(final PackOutput output, final ExistingFileHelper fileHelper, final CompletableFuture<Provider> registries) {
		super(SacksNSuch.MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(final Provider registries, final ExistingFileHelper fileHelper) {
		createEntities("default_slots").addSlots("belt", "back", "feet").addPlayer();
		createSlot("feet").icon(InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
	}
}