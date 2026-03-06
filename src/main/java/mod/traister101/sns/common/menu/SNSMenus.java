package mod.traister101.sns.common.menu;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.util.ItemContainerMenuProvider;
import mod.traister101.sns.util.ItemContainerMenuProvider.ItemContainerFactory;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;

public final class SNSMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SacksNSuch.MODID);

	private static final ItemContainerFactory<ContainerItemMenu> MENU_FACTORY = ItemContainerFactory.of(ContainerItemMenu::forHeld,
			ContainerItemMenu::forInventory, ContainerItemMenu::forCurios);

	public static final RegistryObject<MenuType<ContainerItemMenu>> CONTAINER_ITEM_MENU = MENUS.register("container_item_menu",
			() -> IForgeMenuType.create(ItemContainerMenuProvider.fromNetwork(MENU_FACTORY)));

	public static final ItemContainerMenuProvider CONTAINER_ITEM_MENU_PROVIDER = new ItemContainerMenuProvider(MENU_FACTORY);
}