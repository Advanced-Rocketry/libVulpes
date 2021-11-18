package zmaster587.libVulpes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;


public interface INetworkMachine {
	
	//Writes data to the network given an id of what type of packet to write
	void writeDataToNetwork(PacketBuffer out, byte id);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	void readDataFromNetwork(PacketBuffer in, byte packetId, CompoundNBT nbt);

	//Applies changes from network
	void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt);
}
