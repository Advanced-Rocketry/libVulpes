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

	public PacketSpawnEntity(int entityId, UUID uuid, double xPos, double yPos, double zPos, float pitch, float yaw, EntityType<?> entityType, int entityData, Vector3d speedVector) {
		this.entityId = entityId;
		this.uniqueId = uuid;
		this.x = xPos;
		this.y = yPos;
		this.z = zPos;
		this.pitch = MathHelper.floor(pitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(yaw * 256.0F / 360.0F);
		this.type = entityType;
		this.data = entityData;
		this.speedX = (int)(MathHelper.clamp(speedVector.x, -3.9D, 3.9D) * 8000.0D);
		this.speedY = (int)(MathHelper.clamp(speedVector.y, -3.9D, 3.9D) * 8000.0D);
		this.speedZ = (int)(MathHelper.clamp(speedVector.z, -3.9D, 3.9D) * 8000.0D);
	}

	public PacketSpawnEntity(Entity entity) {
		this(entity, 0);

	}

	public PacketSpawnEntity(Entity entity, CompoundNBT nbt) {
		this(entity, 0);

		this.nbt = nbt;
	}

	public PacketSpawnEntity(Entity entityIn, int typeIn) {
		this(entityIn.getEntityId(), entityIn.getUniqueID(), entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationPitch, entityIn.rotationYaw, entityIn.getType(), typeIn, entityIn.getMotion());
	}

	public PacketSpawnEntity(Entity entity, EntityType<?> entityType, int entityData, BlockPos pos) {
		this(entity.getEntityId(), entity.getUniqueID(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), entity.rotationPitch, entity.rotationYaw, entityType, entityData, entity.getMotion());
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
