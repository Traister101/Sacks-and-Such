package mod.traister101.sacks.objects.entity.projectile;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.util.PowderKegExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityExplosiveVessel extends EntityArrow implements IEntityAdditionalSpawnData {

    private VesselType type;
    private float strength;
    private boolean isSticky;
    private int fuse;

    public EntityExplosiveVessel(World worldIn) {
        super(worldIn);
        setSize(0.5F, 0.5F);
        this.setDamage(0);
    }

    public EntityExplosiveVessel(World worldIn, double x, double y, double z) {
        this(worldIn);
        setPosition(x, y, z);
    }

    public EntityExplosiveVessel(World worldIn, EntityLivingBase throwerIn, float strength, VesselType type) {
        this(worldIn, throwerIn.posX, throwerIn.posY + (double) throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        this.shootingEntity = throwerIn;
        this.strength = strength;
        this.type = type;
        if (type == VesselType.STICKY) {
            this.isSticky = true;
            fuse = 80;
        } else this.isSticky = false;
    }

    @Override
    protected void onHit(RayTraceResult result) {

        if (result.typeOfHit == RayTraceResult.Type.BLOCK && isSticky) {
            motionX = result.hitVec.x - posX;
            motionY = result.hitVec.y - posY;
            motionZ = result.hitVec.z - posZ;
            double f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            posX -= motionX / f2 * 0.05000000074505806D;
            posY -= motionY / f2 * 0.05000000074505806D;
            posZ -= motionZ / f2 * 0.05000000074505806D;
            playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
            inGround = true;
            return;
        }

        if (world.isRemote) return;
        world.setEntityState(this, (byte) 3);
        explode();
    }

    @Override
    public void onUpdate() {

        if (inGround) {
            --fuse;
            if (fuse <= 0) if (!world.isRemote) explode();
        }
        super.onUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("fuse", fuse);
        compound.setBoolean("sticky", isSticky);
        compound.setInteger("type", type.ordinal());
        compound.setFloat("strength", strength);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fuse = compound.getInteger("fuse");
        isSticky = compound.getBoolean("sticky");
        type = VesselType.valueOf(compound.getInteger("type"));
        strength = compound.getFloat("strength");
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(isSticky);
        buffer.writeInt(fuse);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        isSticky = buffer.readBoolean();
        fuse = buffer.readInt();
        type = VesselType.valueOf(buffer.readInt());
    }

    protected void explode() {
        PowderKegExplosion explosion = new PowderKegExplosion(world, this, posX, posY, posZ, strength);
        if (ForgeEventFactory.onExplosionStart(world, explosion)) return;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        setDead();
    }

    public VesselType getType() {
        return type;
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }
}