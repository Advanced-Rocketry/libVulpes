package zmaster587.libVulpes.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;

public interface INetworkMachine {
	
	//Writes data to the network given an id of what type of packet to write
	public void writeDataToNetwork(ByteBuf out, byte id);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	public void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt);
	
	//Applies changes from network
	public void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt);
}
