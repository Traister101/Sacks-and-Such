package mod.traister101.sacks.objects.entity.projectile;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.util.PowderKegExplosion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

// TODO figure out why server sends a bad location packet making the client desync
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityExplosiveVessel extends Entity implements IEntityAdditionalSpawnData, IProjectile {

	public Entity ignoreEntity;
	protected boolean inGround;
	private int xTile;
	private int yTile;
	private int zTile;
	private Block inBlock;
	private int ignoreTime;
	private VesselType type;
	private float strength;
	private int fuse;

	public EntityExplosiveVessel(World worldIn) {
		super(worldIn);
		setSize(0.5F, 0.5F);
		xTile = yTile = zTile = -1;
	}

	public EntityExplosiveVessel(World worldIn, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
	}

	public EntityExplosiveVessel(World worldIn, EntityLivingBase thrower, float strength, VesselType type) {
		this(worldIn, thrower.posX, thrower.posY + (double) thrower.getEyeHeight() - 0.10000000149011612D, thrower.posZ);
		this.strength = strength;
		this.type = type;
		if (type == VesselType.STICKY) {
			this.fuse = (int) Math.max(strength * 5, 40);
		}
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (prevRotationPitch == 0 && prevRotationYaw == 0) {
			final float horizontalMotion = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
			rotationPitch = (float) (MathHelper.atan2(motionY, horizontalMotion) * (180D / Math.PI));
			prevRotationYaw = rotationYaw;
			prevRotationPitch = rotationPitch;
		}

		final BlockPos blockPos = new BlockPos(xTile, yTile, zTile);
		final IBlockState blockState = world.getBlockState(blockPos);

		if (inGround) {

			if (blockState.getBlock() == inBlock && world.collidesWithAnyBlock(getEntityBoundingBox().grow(0.05))) {
				--fuse;

				if (fuse <= 0) {
					explode();
				}

				return;
			}

			inGround = false;
			motionX *= rand.nextFloat() * 0.2F;
			motionY *= rand.nextFloat() * 0.2F;
			motionZ *= rand.nextFloat() * 0.2F;
		}

		final Vec3d entityVectorStart = new Vec3d(posX, posY, posZ);
		Vec3d entityVectorEnd = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
		RayTraceResult collisionResult = world.rayTraceBlocks(entityVectorStart, entityVectorEnd, false, true, false);

		// entityVectorEnd should be the block collision
		if (collisionResult != null) {
			entityVectorEnd = new Vec3d(collisionResult.hitVec.x, collisionResult.hitVec.y, collisionResult.hitVec.z);
		}

		final List<Entity> collideableEntities = world.getEntitiesWithinAABBExcludingEntity(this,
				getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1));

		boolean ignoreEntityCollision = false;

		for (Entity entity : collideableEntities) {
			if (entity.canBeCollidedWith()) {

				if (entity == ignoreEntity) {
					ignoreEntityCollision = true;
					continue;
				}

				if (ticksExisted < 2 && ignoreEntity == null) {
					ignoreEntity = entity;
					ignoreEntityCollision = true;
					continue;
				}

				ignoreEntityCollision = false;

				final AxisAlignedBB hitBox = entity.getEntityBoundingBox().grow(1);
				final RayTraceResult entityCollisionResult = hitBox.calculateIntercept(entityVectorStart, entityVectorEnd);

				if (entityCollisionResult != null) {

					if (entityVectorStart.squareDistanceTo(entityCollisionResult.hitVec) < 0) {
						collisionResult = new RayTraceResult(entity);
					}
				}
			}
		}

		if (ignoreEntity != null) {
			if (ignoreEntityCollision) {
				ignoreTime = 2;
			} else if (ignoreTime-- <= 0) {
				ignoreEntity = null;
			}
		}

		if (collisionResult != null) {
			if (collisionResult.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(collisionResult.getBlockPos())
					.getBlock() == Blocks.PORTAL) {
				setPortal(collisionResult.getBlockPos());
			} else if (!MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(this, collisionResult))) {
				onImpact(collisionResult);
			}
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;

		rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
		final float horizontalMotion = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		rotationPitch = (float) (MathHelper.atan2(motionY, horizontalMotion) * (180D / Math.PI));

		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}

		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}

		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}

		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		final float airFriction;

		if (isInWater()) {
			for (int j = 0; j < 4; ++j) {
				world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX,
						motionY, motionZ);
			}

			// Slower when in water
			airFriction = 0.8F;
		} else airFriction = 0.99F;

		motionX *= airFriction;
		motionY *= airFriction;
		motionZ *= airFriction;

		if (!hasNoGravity()) {
			motionY -= getGravityVelocity();
		}

		setPosition(posX, posY, posZ);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);

		if (this.inGround) {
			xTile = MathHelper.floor(posX);
			yTile = MathHelper.floor(posY);
			zTile = MathHelper.floor(posZ);
		}
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound compound) {
		xTile = compound.getInteger("xTile");
		yTile = compound.getInteger("yTile");
		zTile = compound.getInteger("zTile");

		if (compound.hasKey("inBlock")) {
			inBlock = Block.getBlockFromName(compound.getString("inBlock"));
		} else {
			inBlock = Block.getBlockById(compound.getByte("inBlock") & 255);
		}

		inGround = compound.getBoolean("inGround");

		fuse = compound.getInteger("fuse");
		type = VesselType.getEmum(compound.getInteger("type"));
		strength = compound.getFloat("strength");
	}

	@Override
	public void writeEntityToNBT(final NBTTagCompound compound) {
		compound.setInteger("xTile", xTile);
		compound.setInteger("yTile", yTile);
		compound.setInteger("zTile", zTile);

		final ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(inBlock);
		compound.setString("inBlock", resourcelocation.toString());
		compound.setBoolean("inGround", inGround);

		compound.setInteger("fuse", fuse);
		compound.setInteger("type", type.ordinal());
		compound.setFloat("strength", strength);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		super.setVelocity(x, y, z);

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			final float horizontalAngle = MathHelper.sqrt(x * x + z * z);
			rotationPitch = (float) (MathHelper.atan2(y, horizontalAngle) * (180D / Math.PI));
			rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}

	protected void onImpact(RayTraceResult result) {

		if (!type.isSticky) {
			explode();
			return;
		}

		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			final BlockPos blockpos = result.getBlockPos();
			xTile = blockpos.getX();
			yTile = blockpos.getY();
			zTile = blockpos.getZ();

			final IBlockState blockState = world.getBlockState(blockpos);
			inBlock = blockState.getBlock();

			motionX = result.hitVec.x - posX;
			motionY = result.hitVec.y - posY;
			motionZ = result.hitVec.z - posZ;
			markVelocityChanged();

			final double someSortaMagicNumber = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			posX -= motionX / someSortaMagicNumber * 0.05000000074505806D;
			posY -= motionY / someSortaMagicNumber * 0.05000000074505806D;
			posZ -= motionZ / someSortaMagicNumber * 0.05000000074505806D;

			playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
			inGround = true;

			if (blockState.getMaterial() != Material.AIR) {
				inBlock.onEntityCollision(world, blockpos, blockState, this);
			}
		}

		world.setEntityState(this, (byte) 3);
	}

	/**
	 * Magic number for gravity
	 */
	private float getGravityVelocity() {
		return 0.03F;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		final float yaw = MathHelper.sqrt(x * x + y * y + z * z);
		x /= yaw;
		y /= yaw;
		z /= yaw;
		x += rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
		y += rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
		z += rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
		motionX = x *= velocity;
		motionY = y *= velocity;
		motionZ = z *= velocity;
		final float pitch = MathHelper.sqrt(x * x + z * z);
		prevRotationYaw = rotationYaw = (float) (MathHelper.atan2(x, z) * (180 / Math.PI));
		prevRotationPitch = rotationPitch = (float) (MathHelper.atan2(y, pitch) * (180 / Math.PI));
	}

	/**
	 * Sets throwable heading based on an entity that's throwing it
	 */
	public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
		final float x = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
		final float y = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
		final float z = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
		shoot(x, y, z, velocity, inaccuracy);
		motionX += entityThrower.motionX;
		motionZ += entityThrower.motionZ;

		if (!entityThrower.onGround) {
			motionY += entityThrower.motionY;
		}
	}

	@Override
	public void writeSpawnData(final ByteBuf buffer) {
		buffer.writeInt(fuse);
		buffer.writeInt(type.ordinal());
	}

	@Override
	public void readSpawnData(final ByteBuf buffer) {
		fuse = buffer.readInt();
		type = VesselType.getEmum(buffer.readInt());
	}

	protected void explode() {
		if (!world.isRemote) {
			final PowderKegExplosion explosion = new PowderKegExplosion(world, this, posX, posY, posZ, strength);
			if (ForgeEventFactory.onExplosionStart(world, explosion)) return;
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			setDead();
		}
	}

	public VesselType getType() {
		return type;
	}
}