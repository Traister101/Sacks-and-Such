package mod.traister101.sacks.objects.items;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.types.SackType;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSack extends Item implements IItemSize {

	private final SackType type;

	public ItemSack(final SackType type) {
		setMaxStackSize(1);
		this.type = type;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack heldStack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (player.isSneaking()) {
				if (ConfigSNS.GLOBAL.shiftClickTogglesVoid) {
					if (type.doesVoiding()) {
						SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoVoid(heldStack), ToggleType.VOID);
					} else {
						final TextComponentTranslation status = new TextComponentTranslation(SacksNSuch.MODID + ".sack.no_void");
						Minecraft.getMinecraft().player.sendStatusMessage(status, true);
					}
				} else {
					if (type.doesAutoPickup()) {
						SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoPickup(heldStack), ToggleType.PICKUP);
					} else {
						final TextComponentTranslation status = new TextComponentTranslation(SacksNSuch.MODID + ".sack.no_pickup");
						Minecraft.getMinecraft().player.sendStatusMessage(status, true);
					}
				}
			} else {
				GuiHandler.openGui(world, player, GuiType.SACK);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, heldStack);
	}

	@Override
	public void addInformation(final ItemStack itemStack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flagIn) {
		String text = SacksNSuch.MODID + ".sack.tooltip";
		if (GuiScreen.isShiftKeyDown()) {
			if (NBTHelper.isAutoVoid(itemStack) && type.doesVoiding()) {
				text += ".void";
			}
			if (NBTHelper.isAutoPickup(itemStack) && type.doesAutoPickup()) {
				text += ".pickup";
			}
			text += ".shift";
		}
		tooltip.add(I18n.format(text));
	}

	@Override
	public boolean hasEffect(final ItemStack itemStack) {
		if (ConfigSNS.GLOBAL.voidGlint) return NBTHelper.isAutoVoid(itemStack);
		return NBTHelper.isAutoPickup(itemStack);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(final ItemStack itemStack, @Nullable NBTTagCompound nbt) {
		return new SackHandler(nbt, type);
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return type.getSize(itemStack);
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return Weight.VERY_HEAVY;
	}

	@Override
	public boolean canStack(final ItemStack stack) {
		return false;
	}

	public SackType getType() {
		return type;
	}
}