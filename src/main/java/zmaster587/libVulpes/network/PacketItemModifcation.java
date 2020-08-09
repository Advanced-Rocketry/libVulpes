package zmaster587.libVulpes.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketItemModifcation {

	public static void encode(PacketItemModifcation pkt, PacketBuffer buf)
	{
		pkt.write(buf);
	}
	
	public static PacketItemModifcation decode( PacketBuffer buf)
	{
		PacketItemModifcation pkt = new PacketItemModifcation();
		 
		pkt.read(buf);
		return pkt;
	}
	
	public static class Handler 
	{
		public static void handle(PacketItemModifcation msg, Supplier<NetworkEvent.Context> ctx)
		{
			if(ctx.get().getDirection().getReceptionSide().isServer())
				ctx.get().enqueueWork(() -> msg.executeServer(ctx.get().getSender()));
			else
				ctx.get().enqueueWork(() -> msg.executeClient(Minecraft.getInstance().player));
			
			ctx.get().setPacketHandled(true);
			
		}
	}
	
	CompoundNBT nbt;

	byte packetId;
	int entityId;
	PlayerEntity entity;
	INetworkItem machine;

	public PacketItemModifcation() {
		nbt = new CompoundNBT();
	};

	public PacketItemModifcation(INetworkItem machine, PlayerEntity entity, byte packetId) {
		this();
		this.machine = machine;
		this.entity = entity;
		this.packetId = packetId;
		this.entityId = entity.getEntityId();
	}


	public PacketItemModifcation(INetworkItem machine, PlayerEntity entity, byte packetId, CompoundNBT nbt) {
		this(machine, entity, packetId);
		this.nbt = nbt;
	}


	private void write(PacketBuffer out) {
		BasePacket.writeWorld(out, entity.world);
		out.writeInt(entity.getEntityId());
		out.writeByte(packetId);

		out.writeBoolean(!nbt.isEmpty());

		if(!nbt.isEmpty()) {
			out.writeCompoundTag(nbt);
		}

		machine.writeDataToNetwork(out, packetId, entity.getHeldItem(Hand.MAIN_HAND));
	}

	public void read(PacketBuffer in) {
		//DEBUG:
		World world = BasePacket.readWorld(in);

		int entityId = in.readInt();
		packetId = in.readByte();

		Entity ent = world.getEntityByID(entityId);
		if(ent == null) {
			for(Entity e : world.getPlayers()) {
				if(e.getEntityId() == entityId) {
					ent = e;
					break;
				}
			}
		}

		if(in.readBoolean()) {
			CompoundNBT nbt = null;
			nbt = in.readCompoundTag();
			this.nbt = nbt;
		}

		if(ent != null && ent instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)ent).getHeldItem(Hand.MAIN_HAND);
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).readDataFromNetwork(in, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
	}

	public void execute(PlayerEntity player, Dist side) {

		if(player != null) {
			ItemStack itemStack = player.getHeldItem(Hand.MAIN_HAND);
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).useNetworkData(player, side, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
	}


	public void executeServer(ServerPlayerEntity player) {
		execute((PlayerEntity)player, Dist.DEDICATED_SERVER);
	}


	public void executeClient(PlayerEntity player) {
		execute((PlayerEntity)player, Dist.CLIENT);
	}

	@OnlyIn(value=Dist.CLIENT)
	public void readClient(PacketBuffer in) {
		PacketBuffer buffer = new PacketBuffer(in);

		//DEBUG:
		World world;

		BasePacket.readWorld(in);
		world = Minecraft.getInstance().world;

		int entityId = buffer.readInt();
		packetId = buffer.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(buffer.readBoolean()) {
			CompoundNBT nbt = null;
			nbt = buffer.readCompoundTag();
			this.nbt = nbt;
		}

		if(ent != null && ent instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)ent).getHeldItem(Hand.MAIN_HAND);
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).readDataFromNetwork(in, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
	}
}
