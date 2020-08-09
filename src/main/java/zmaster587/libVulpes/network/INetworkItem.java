package zmaster587.libVulpes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

public interface INetworkItem {
	
	//Writes data to the network given an id of what type of packet to write
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	public void readDataFromNetwork(ByteBuf in, byte packetId, CompoundNBT nbt, ItemStack stack);
	
	//Applies changes from network
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt, ItemStack stack);
}
