package zmaster587.libVulpes.network;

import zmaster587.libVulpes.interfaces.INetworkEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketEntity extends BasePacket {
	
	
	INetworkEntity entity;

	CompoundNBT nbt;
	int entityId;
	byte packetId;

	public PacketEntity() {
		nbt = new CompoundNBT();
	}

	public PacketEntity(INetworkEntity machine, byte packetId) {
		this();
		this.entity = machine;
		this.packetId = packetId;
	}


	public PacketEntity(INetworkEntity entity, byte packetId, CompoundNBT nbt) {
		this(entity, packetId);
		this.nbt = nbt;
	}

	@Override
	public void write(PacketBuffer out) {
		BasePacket.writeWorld(out, ((Entity)entity).world);
		out.writeInt(((Entity)entity).getEntityId());
		out.writeByte(packetId);

		out.writeBoolean(!nbt.isEmpty());

		if(!nbt.isEmpty()) {
			out.writeCompoundTag(nbt);
		}

		entity.writeDataToNetwork(out, packetId);
	}
	
	@Override
	public void read(PacketBuffer in) {
		//DEBUG:
		World world = BasePacket.readWorld(in);

		int entityId = in.readInt();
		packetId = in.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(in.readBoolean()) {
			CompoundNBT nbt;
			nbt = in.readCompoundTag();
			this.nbt = nbt;
		}

		if(ent instanceof INetworkEntity) {
			entity = (INetworkEntity)ent;
			entity.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}

	public void execute(PlayerEntity player, Dist side) {
		if(entity != null)
			entity.useNetworkData(player, side, packetId, nbt);
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {
		execute(player, Dist.DEDICATED_SERVER);
	}

	@Override
	public void executeClient(PlayerEntity player) {
		execute(player, Dist.CLIENT);
		if(entity == null) {
			
		}
	}

	
	@OnlyIn(value=Dist.CLIENT)
	public void readClient(PacketBuffer in) {
		PacketBuffer buffer = new PacketBuffer(in);

		//DEBUG:
		World world;

		in.readResourceLocation();
		world = Minecraft.getInstance().world;


		int entityId = buffer.readInt();
		packetId = buffer.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(buffer.readBoolean()) {
			CompoundNBT nbt;
			nbt = buffer.readCompoundTag();

			this.nbt = nbt;
		}

		if(ent instanceof INetworkEntity) {
			entity = (INetworkEntity)ent;
			entity.readDataFromNetwork(buffer, packetId, nbt);
		}
		else {
			this.entityId = entityId;
			System.out.println("oh no...");
		}
	}

}
