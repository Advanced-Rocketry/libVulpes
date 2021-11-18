package zmaster587.libVulpes.network;

import zmaster587.libVulpes.util.InputSyncHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class PacketChangeKeyState extends BasePacket {
	
	int key;
	boolean state;
	
	public PacketChangeKeyState(int key, boolean state) {
		this.key = key;
		this.state = state;
	}
	
	public PacketChangeKeyState() {}
	

	@Override
	public void write(PacketBuffer out) {
		out.writeInt(key);
		out.writeBoolean(state);
	}

	@Override
	public void readClient(PacketBuffer in) {
		in.readInt();
		in.readBoolean();
	}

	@Override
	public void read(PacketBuffer in) {
		key = in.readInt();
		state = in.readBoolean();
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {
		InputSyncHandler.updateKeyPress(player, key, state);
	}

}
