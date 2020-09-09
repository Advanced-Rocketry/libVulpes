package zmaster587.libVulpes.network;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

public class PacketSpawnEntity extends BasePacket implements IPacket<INetHandler> {
	private int entityId;
	private UUID uniqueId;
	private double x;
	private double y;
	private double z;
	private int speedX;
	private int speedY;
	private int speedZ;
	private int pitch;
	private int yaw;
	private EntityType<?> type;
	private int data;
	private CompoundNBT nbt;

	public PacketSpawnEntity() {
	}

	public PacketSpawnEntity(int p_i50777_1_, UUID p_i50777_2_, double p_i50777_3_, double p_i50777_5_, double p_i50777_7_, float p_i50777_9_, float p_i50777_10_, EntityType<?> p_i50777_11_, int p_i50777_12_, Vector3d p_i50777_13_) {
		this.entityId = p_i50777_1_;
		this.uniqueId = p_i50777_2_;
		this.x = p_i50777_3_;
		this.y = p_i50777_5_;
		this.z = p_i50777_7_;
		this.pitch = MathHelper.floor(p_i50777_9_ * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(p_i50777_10_ * 256.0F / 360.0F);
		this.type = p_i50777_11_;
		this.data = p_i50777_12_;
		this.speedX = (int)(MathHelper.clamp(p_i50777_13_.x, -3.9D, 3.9D) * 8000.0D);
		this.speedY = (int)(MathHelper.clamp(p_i50777_13_.y, -3.9D, 3.9D) * 8000.0D);
		this.speedZ = (int)(MathHelper.clamp(p_i50777_13_.z, -3.9D, 3.9D) * 8000.0D);
	}

	public PacketSpawnEntity(Entity p_i50778_1_) {
		this(p_i50778_1_, 0);

	}

	public PacketSpawnEntity(Entity p_i50778_1_, CompoundNBT nbt) {
		this(p_i50778_1_, 0);

		this.nbt = nbt;
	}

	public PacketSpawnEntity(Entity entityIn, int typeIn) {
		this(entityIn.getEntityId(), entityIn.getUniqueID(), entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationPitch, entityIn.rotationYaw, entityIn.getType(), typeIn, entityIn.getMotion());
	}

	public PacketSpawnEntity(Entity p_i50779_1_, EntityType<?> p_i50779_2_, int p_i50779_3_, BlockPos p_i50779_4_) {
		this(p_i50779_1_.getEntityId(), p_i50779_1_.getUniqueID(), (double)p_i50779_4_.getX(), (double)p_i50779_4_.getY(), (double)p_i50779_4_.getZ(), p_i50779_1_.rotationPitch, p_i50779_1_.rotationYaw, p_i50779_2_, p_i50779_3_, p_i50779_1_.getMotion());
	}




	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void read(PacketBuffer buf) {
		this.entityId = buf.readVarInt();
		this.uniqueId = buf.readUniqueId();
		this.type = Registry.ENTITY_TYPE.getByValue(buf.readVarInt());
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.data = buf.readInt();
		this.speedX = buf.readShort();
		this.speedY = buf.readShort();
		this.speedZ = buf.readShort();
		boolean hasNBT = buf.readBoolean();
		if(hasNBT)
		{
			nbt = buf.readCompoundTag();
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void write(PacketBuffer buf) {
		buf.writeVarInt(this.entityId);
		buf.writeUniqueId(this.uniqueId);
		buf.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeInt(this.data);
		buf.writeShort(this.speedX);
		buf.writeShort(this.speedY);
		buf.writeShort(this.speedZ);
		buf.writeBoolean(nbt != null);
		if(nbt != null)
		{
			buf.writeCompoundTag(nbt);
		}
	}

	@Override
	protected void readClient(PacketBuffer in) {
		read(in);
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

	@Override
	public void executeClient(PlayerEntity player) {
		Entity entity = type.create(Minecraft.getInstance().world);
		entity.setPositionAndRotation(x, y, z, yaw, pitch);
		entity.setUniqueId(uniqueId);
		entity.setVelocity(speedX, speedY, speedZ);
		Minecraft.getInstance().world.addEntity(entityId, entity);
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		read(buf);

	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		write(buf);
	}

	@Override
	public void processPacket(INetHandler handler) {
		
		// spinlock until ready
		while(Minecraft.getInstance().world == null);
		
		Entity entity = type.create(Minecraft.getInstance().world);
		entity.setUniqueId(uniqueId);
		entity.setVelocity(speedX, speedY, speedZ);
		entity.setEntityId(entityId);
		
		if(nbt != null && entity instanceof IEntitySpawnNBT)
			((IEntitySpawnNBT) entity).readSpawnNBT(nbt);
		
		entity.setPositionAndRotation(x, y, z, yaw, pitch);
		Minecraft.getInstance().world.addEntity(entityId, entity);
		entity.setPositionAndRotation(x, y, z, yaw, pitch);
		entity.setPacketCoordinates(x, y, z);
	}
}
