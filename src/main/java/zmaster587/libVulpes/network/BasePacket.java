package zmaster587.libVulpes.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public abstract class BasePacket {


	// Since Forge's packet handling stuff seems to be strictly tied to the class being sent and I really
	// want to keep it simple, the "BasePacket" will be the only class to touch the forge packet system
	// but we need a way to save the data, since java functions act like virtuals we can reference the
	// actual packet and data we want to send here while the current instance of basepacket can still be a real
	// Base packet type (getClass() returns BasePacket.class), we get the real class back in decode
	private BasePacket secretHiddenPacket;

	public static void writeWorld(PacketBuffer out, World world)
	{
		out.writeResourceLocation(world.func_234923_W_().getRegistryName());
	}

	public static World readWorld(PacketBuffer in)
	{
		ResourceLocation worldRegisteryResourceLocation = in.readResourceLocation();
		RegistryKey<World> worldReg = null;
		for(RegistryKey<World> key : ServerLifecycleHooks.getCurrentServer().func_240770_D_())
		{
			if(key.getRegistryName() == worldRegisteryResourceLocation)
			{
				worldReg = key;
				break;
			}
		}

		World world = ServerLifecycleHooks.getCurrentServer().getWorld(worldReg);

		return world;
	}

	protected abstract void write(PacketBuffer out);
	protected abstract void read(PacketBuffer in);
	protected abstract void readClient(PacketBuffer in);
	public abstract void executeServer(ServerPlayerEntity player);
	public abstract void executeClient(PlayerEntity player);
}
