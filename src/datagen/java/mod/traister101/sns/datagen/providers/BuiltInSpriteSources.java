package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.data.*;

import java.util.Optional;

public class BuiltInSpriteSources extends SpriteSourceProvider {

	public BuiltInSpriteSources(final PackOutput output, final ExistingFileHelper fileHelper) {
		super(output, fileHelper, SacksNSuch.MODID);
	}

	@Override
	protected void addSources() {
		atlas(BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation(SacksNSuch.MODID, "loom/reinforced_fabric"), Optional.empty()));
	}
}