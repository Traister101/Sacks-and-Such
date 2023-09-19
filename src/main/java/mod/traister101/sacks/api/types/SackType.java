package mod.traister101.sacks.api.types;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SackType extends IForgeRegistryEntry.Impl<SackType> {

	private int slotCount;
	private int slotCapacity;
	private boolean doesAutoPickup;
	private boolean doesVoiding;
	private Size allowedSize;

	/**
	 * A type of sack, used to cleanly sync server config to the client, so we can safely have dynamic size containers controlled via config.
	 * Everything registered here will have its fields synced to the client on login to the world.
	 *
	 * @param slotCount The amount of slots the sack should have
	 * @param slotCapacity The capacity of each slot
	 * @param doAutoPickup If this sack should have automatic pickup capabilities
	 * @param doesVoiding If this sack should have voiding capabilities
	 * @param allowedSize The maximum {@link Size} allowed inside the sack
	 */
	public SackType(final int slotCount, final int slotCapacity, final boolean doAutoPickup, final boolean doesVoiding, final Size allowedSize) {
		this.slotCount = slotCount;
		this.slotCapacity = slotCapacity;
		this.doesAutoPickup = doAutoPickup;
		this.doesVoiding = doesVoiding;
		this.allowedSize = allowedSize;
	}

	public int getSlotCount() {
		return slotCount;
	}

	@SideOnly(Side.CLIENT)
	public void setSlotCount(final int slotCount) {
		this.slotCount = slotCount;
	}

	public int getSlotCapacity() {
		return slotCapacity;
	}

	@SideOnly(Side.CLIENT)
	public void setSlotCapacity(final int slotCapacity) {
		this.slotCapacity = slotCapacity;
	}

	public boolean doesAutoPickup() {
		return doesAutoPickup;
	}

	@SideOnly(Side.CLIENT)
	public void setDoesAutoPickup(final boolean doesAutoPickup) {
		this.doesAutoPickup = doesAutoPickup;
	}

	public boolean doesVoiding() {
		return doesVoiding;
	}

	@SideOnly(Side.CLIENT)
	public void setDoesVoiding(final boolean doesVoiding) {
		this.doesVoiding = doesVoiding;
	}

	public Size getAllowedSize() {
		return allowedSize;
	}

	@SideOnly(Side.CLIENT)
	public void setAllowedSize(final Size allowedSize) {
		this.allowedSize = allowedSize;
	}
}