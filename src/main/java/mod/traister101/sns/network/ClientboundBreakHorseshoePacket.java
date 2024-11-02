package mod.traister101.sns.network;

import mod.traister101.sns.common.items.HorseshoesItem;
import mod.traister101.sns.mixins.common.acessor.AbstractHorseInventoryAcessor;
import net.dries007.tfc.client.ClientHelpers;

import net.minecraft.core.particles.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundBreakHorseshoePacket {

	private final int id;

	public ClientboundBreakHorseshoePacket(final AbstractHorse horse) {
		this.id = horse.getId();
	}

	ClientboundBreakHorseshoePacket(final FriendlyByteBuf friendlyByteBuf) {
		this.id = friendlyByteBuf.readVarInt();
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeVarInt(id);
	}

	void handle() {
		final Level level = ClientHelpers.getLevel();
		if (level == null) return;

		final Entity entity = level.getEntity(id);
		if (!(entity instanceof final AbstractHorse horse)) return;

		final ItemStack item = ((AbstractHorseInventoryAcessor) horse).getInventory().getItem(HorseshoesItem.getHorseshoesSlot(horse));

		if (!item.isEmpty()) {
			if (!horse.isSilent()) {
				level.playLocalSound(horse.getX(), horse.getY(), horse.getZ(), SoundEvents.ITEM_BREAK, horse.getSoundSource(), 0.8F,
						0.8F + level.random.nextFloat() * 0.4F, false);
			}

			// Item break particles
			for (int i = 0; i < 5; ++i) {
				final var vec3 = new Vec3(((double) level.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0).xRot(
						-horse.getXRot() * ((float) Math.PI / 180)).yRot(-horse.getYRot() * ((float) Math.PI / 180));

				final var pos = new Vec3(((double) level.random.nextFloat() - 0.5) * 0.3, (double) (-level.random.nextFloat()) * 0.6 - 0.3, 0.6).xRot(
								-horse.getXRot() * ((float) Math.PI / 180))
						.yRot(-horse.getYRot() * ((float) Math.PI / 180))
						.add(horse.getX(), horse.getEyeY(), horse.getZ());

				if (horse.level() instanceof ServerLevel) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
					((ServerLevel) horse.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), pos.x, pos.y, pos.z, 1, vec3.x,
							vec3.y + 0.05, vec3.z, 0);
				else horse.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pos.x, pos.y, pos.z, vec3.x, vec3.y + 0.05, vec3.z);
			}
		}

	}
}