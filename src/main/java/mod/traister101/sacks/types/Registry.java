package mod.traister101.sacks.types;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.registries.SNSRegistryName;
import mod.traister101.sacks.api.registries.SackRegistry;
import mod.traister101.sacks.api.types.SackType;
import net.dries007.tfc.api.registries.TFCRegistryEvent.RegisterPreBlock;
import net.minecraft.block.Block;
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
	public static void onBlockRegister(final Register<Block> registryEvent) {
		MinecraftForge.EVENT_BUS.post(new RegisterPreBlock<>(SNSRegistryName.SACK, SackRegistry.SACK_TYPES));
	}
}