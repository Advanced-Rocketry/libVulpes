package zmaster587.libVulpes.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.libVulpes.util.ZUtils;

public abstract class BasePacket {


	// Since Forge's packet handling stuff seems to be strictly tied to the class being sent and I really
	// want to keep it simple, the "BasePacket" will be the only class to touch the forge packet system
	// but we need a way to save the data, since java functions act like virtuals we can reference the
	// actual packet and data we want to send here while the current instance of basepacket can still be a real
	// Base packet type (getClass() returns BasePacket.class), we get the real class back in decode
	private BasePacket secretHiddenPacket;

	public static void writeWorld(PacketBuffer out, World world)
	{
		out.writeResourceLocation(world.getDimensionKey().getLocation());
	}

	public static World readWorld(PacketBuffer in)
	{
		
		ResourceLocation worldRegisteryResourceLocation = in.readResourceLocation();
		return ZUtils.getWorld(worldRegisteryResourceLocation);
	}

	protected abstract void write(PacketBuffer out);
	protected abstract void read(PacketBuffer in);
	protected abstract void readClient(PacketBuffer in);
	public abstract void executeServer(ServerPlayerEntity player);
	public abstract void executeClient(PlayerEntity player);
}
