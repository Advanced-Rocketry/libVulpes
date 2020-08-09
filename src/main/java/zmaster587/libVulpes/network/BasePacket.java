package zmaster587.libVulpes.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BasePacket {

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
}
