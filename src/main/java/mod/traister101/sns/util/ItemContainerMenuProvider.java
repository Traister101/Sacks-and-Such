package mod.traister101.sns.util;

import mod.traister101.sns.util.ItemSlotData.*;
import net.dries007.tfc.util.Helpers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraftforge.network.IContainerFactory;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@AllArgsConstructor
public class ItemContainerMenuProvider {

	private final ItemContainerFactory<?> factory;
	private final @Nullable Component name;

	public ItemContainerMenuProvider(final ItemContainerFactory<?> factory) {
		this(factory, null);
	}

	public static <M extends AbstractContainerMenu> IContainerFactory<M> fromNetwork(final ItemContainerFactory<M> factory) {
		return (windowId, inv, data) -> factory.create(windowId, inv, ItemSlotData.read(data));
	}

	public void openMenu(final Player player, final ItemSlotData slotData) {
		openMenu(player, slotData, friendlyByteBuf -> {});
	}

	public void openMenu(final Player playerIn, final ItemSlotData slotData, final Consumer<FriendlyByteBuf> additionalData) {
		if (!(playerIn instanceof ServerPlayer serverPlayer)) return;
		final var title = name == null ? slotData.stack(serverPlayer).getHoverName() : name;
		final var provider = new SimpleMenuProvider((windowId, inventory, unused) -> this.factory.create(windowId, inventory, slotData), title);
		Helpers.openScreen(serverPlayer, provider, additionalData.andThen(slotData::write));
	}

	@FunctionalInterface
	public interface ItemContainerFactory<M extends AbstractContainerMenu> {

		static <M extends AbstractContainerMenu> ItemContainerFactory<M> of(final HeldContainerFactory<M> heldContainerFactory,
				final InventoryContainerFactory<M> inventoryContainerFactory, final CuriosContainerFactory<M> curiosContainerFactory) {
			return (windowId, inventory, slotData) -> switch (slotData.type()) {
				case HELD -> heldContainerFactory.create(windowId, inventory, ((HeldSlotData) slotData));
				case INVENTORY -> inventoryContainerFactory.create(windowId, inventory, ((InventorySlotData) slotData));
				case CURIOS -> curiosContainerFactory.create(windowId, inventory, ((CuriosSlotData) slotData));
			};
		}

		M create(int windowId, Inventory inventory, ItemSlotData slotData);

		@FunctionalInterface
		interface HeldContainerFactory<M extends AbstractContainerMenu> {

			M create(int windowId, Inventory inventory, HeldSlotData heldSlotData);
		}

		@FunctionalInterface
		interface InventoryContainerFactory<M extends AbstractContainerMenu> {

			M create(int windowId, Inventory inventory, InventorySlotData inventorySlotData);
		}

		@FunctionalInterface
		interface CuriosContainerFactory<M extends AbstractContainerMenu> {

			M create(int windowId, Inventory inventory, CuriosSlotData curiosSlotData);
		}
	}
}