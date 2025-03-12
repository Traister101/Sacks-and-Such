package mod.traister101.sns.common.attribute;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.world.entity.ai.attributes.*;

import net.minecraftforge.registries.*;

public final class SNSAttributes {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SacksNSuch.MODID);

	public static final RegistryObject<Attribute> EXTRA_FALL_DISTANCE = ATTRIBUTES.register("extra_fall_distance",
			() -> new RangedAttribute(SacksNSuch.MODID + ".extra_fall_distance", 0, 0, 64).setSyncable(true));
}