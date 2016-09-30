package zmaster587.libVulpes.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExecutorClient implements Runnable {

	final Side side;
	final BasePacket packet;
	final EntityPlayer player;

	public ExecutorClient(BasePacket packet, EntityPlayer player, Side side) {
		this.packet = packet;
		this.player = player;
		this.side = side;
	}

	@Override
	public void run() {
		if(side.isClient()) {
			packet.executeClient(player);
		}
		else
			packet.executeServer((EntityPlayerMP) player);
	}
}