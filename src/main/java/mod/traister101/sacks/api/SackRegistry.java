package mod.traister101.sacks.api;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.types.SackType;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class SackRegistry {

	public static final IForgeRegistry<SackType> SACK_TYPES = GameRegistry.findRegistry(SackType.class);

	static {
		// Make sure all public static final fields have values, should stop people from prematurely loading this class.
		try {
			final int publicStaticFinal = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

			for (final Field field : SackRegistry.class.getFields()) {
				if (!field.getType().isAssignableFrom(IForgeRegistry.class)) {
					SacksNSuch.getLog().warn("[Please inform developers] Weird field? (Not a registry) {}", field);
					continue;
				}
				if ((field.getModifiers() & publicStaticFinal) != publicStaticFinal) {
					SacksNSuch.getLog().warn("[Please inform developers] Weird field? (not Public Static Final) {}", field);
					continue;
				}
				if (field.get(null) == null) {
					throw new RuntimeException("Oh nooo! Someone tried to use the registries before they exist. Now everything is broken!");
				}
			}
		} catch (Exception e) {
			SacksNSuch.getLog().fatal("Fatal error! This is likely a programming mistake.", e);
			throw new RuntimeException(e);
		}
	}
}