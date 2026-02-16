package mod.traister101.sns.common;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SNSBlockTags {

	/**
	 * Blocks which our boots should prevent slowdown
	 */
	public static final TagKey<Block> BOOTS_PREVENT_SLOWDOWN = create("boots_prevent_slowdown");

	private static TagKey<Block> create(final String name) {
		return TagKey.create(Registries.BLOCK, SacksNSuch.location(name));
	}
}