package zmaster587.libVulpes.interfaces;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import zmaster587.libVulpes.util.INetworkMachine;

public interface INetworkEntity extends INetworkMachine {
	
	//Cannot overwrite Entity
	int getEntityId();

	void writeDataToNetwork(PacketBuffer out, byte id);

	void readDataFromNetwork(PacketBuffer in, byte packetId, CompoundNBT nbt);
}
