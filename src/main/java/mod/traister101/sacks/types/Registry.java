package mod.traister101.sacks.types;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.SNSRegistryName;
import mod.traister101.sacks.api.SackRegistry;
import mod.traister101.sacks.api.registries.SNSRegistryEvent.RegisterSackEvent;
import mod.traister101.sacks.api.types.SackType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = SacksNSuch.MODID)
public final class Registry {

	@SubscribeEvent
	public static void onNewRegistryEvent(final NewRegistry newRegistry) {
		new RegistryBuilder<SackType>().setName(SNSRegistryName.SACK).allowModification().setType(SackType.class).create();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemRegister(final Register<Item> registryEvent) {
		MinecraftForge.EVENT_BUS.post(new RegisterSackEvent(SNSRegistryName.SACK, SackRegistry.SACK_TYPES));
	}
}