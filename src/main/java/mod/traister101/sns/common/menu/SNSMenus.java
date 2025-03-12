package mod.traister101.sns.common.menu;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;

public final class SNSMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SacksNSuch.MODID);

	public static final RegistryObject<MenuType<ContainerItemMenu>> CONTAINER_ITEM_MENU = MENUS.register("container_item_menu",
			() -> IForgeMenuType.create(ContainerItemMenu::fromNetwork));
}