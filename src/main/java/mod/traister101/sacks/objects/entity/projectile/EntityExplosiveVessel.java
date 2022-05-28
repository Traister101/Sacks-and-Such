package mod.traister101.sacks.objects.entity.projectile;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.util.PowderKegExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityExplosiveVessel extends EntityThrowable implements IEntityAdditionalSpawnData {
	
	private final float strength;
	
	public EntityExplosiveVessel(World worldIn) {
		super(worldIn);
		this.strength = 5;
	}
	
	public EntityExplosiveVessel(World worldIn, EntityLivingBase throwerIn, float strength) {
		super(worldIn, throwerIn);
		this.strength = strength;
	}
	
	public EntityExplosiveVessel(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.strength = 5;
	}
	
	protected void explode() {
		if (getEntityWorld().isRemote) return;
		
		PowderKegExplosion explosion = new PowderKegExplosion(getEntityWorld(), this, posX, posY, posZ, strength);
		if (ForgeEventFactory.onExplosionStart(world, explosion)) return;
		explosion.doExplosionA();
		explosion.doExplosionB(true);
	}
	
	protected void onImpact(RayTraceResult result) {
		
		if (world.isRemote) return;
		
			
		world.setEntityState(this, (byte) 3);
		explode();
		setDead();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		
	}
	
	@Override
	public void readSpawnData(ByteBuf additionalData) {
		
	}
}