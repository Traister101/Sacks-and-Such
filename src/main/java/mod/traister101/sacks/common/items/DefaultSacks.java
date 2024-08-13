package mod.traister101.sacks.common.items;

import net.dries007.tfc.common.capabilities.size.Size;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.config.ServerConfig.SackConfig;
import mod.traister101.sacks.util.SackType;

import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public enum DefaultSacks implements SackType {

	THATCH_BASKET(SNSConfig.SERVER.thatchBasket),
	LEATHER_SACK(SNSConfig.SERVER.leatherSack),
	BURLAP_SACK(SNSConfig.SERVER.burlapSack),
	ORE_SACK(SNSConfig.SERVER.oreSack),
	SEED_POUCH(SNSConfig.SERVER.seedPouch),
	FRAME_PACK(SNSConfig.SERVER.framePack);

	private final SackConfig sackConfig;

	DefaultSacks(final SackConfig sackConfig) {
		this.sackConfig = sackConfig;
	}

	@Override
	public int getSlotCount() {
		return sackConfig.slotCount.get();
	}

	@Override
	public int getSlotCapacity() {
		return sackConfig.slotCap.get();
	}

	@Override
	public boolean doesAutoPickup() {
		return sackConfig.doPickup.get();
	}

	@Override
	public boolean doesVoiding() {
		return sackConfig.doVoiding.get();
	}

	@Override
	public Size getAllowedSize() {
		return sackConfig.allowedSize.get();
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		if (this == FRAME_PACK) return Size.HUGE;
		return Size.NORMAL;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}