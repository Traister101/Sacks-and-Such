package mod.traister101.sacks.api.registries;

import mod.traister101.sacks.api.types.SackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class SNSRegistryEvent {

	/**
	 * ** Beware, dirty hack.**
	 *
	 * Required because of this <a href="https://github.com/MinecraftForge/MinecraftForge/issues/4987">issue</a>
	 *
	 * This event is fired from the HIGHEST priority item registry event. This is so when registering items you can easily use the {@link SackType}
	 * when creating our items
	 *
	 * This is against Forge's policy of "Every mod registers its own blocks/items" but TFC does this so whatever LOL
	 */
	public static class RegisterSackEvent extends Register<SackType> {

		public RegisterSackEvent(final ResourceLocation name, final IForgeRegistry<SackType> registry) {
			super(name, registry);
		}
	}
}
