package zmaster587.libVulpes.network;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zmaster587.libVulpes.util.ZUtils;

public class PacketHandler {
	
	private static int discriminatorNumber = 0;
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation("libvulpes", "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	
	public static PacketHandler INSTANCE = new PacketHandler();
	
	public PacketHandler() {
	}

	

	public static final void register() {
		
		HANDLER.registerMessage(discriminatorNumber++, PacketMachine.class, PacketMachine::encode, PacketMachine::decode, PacketMachine.Handler::handle);
		HANDLER.registerMessage(discriminatorNumber++, PacketEntity.class, PacketEntity::encode, PacketEntity::decode, PacketEntity.Handler::handle);
		HANDLER.registerMessage(discriminatorNumber++, PacketChangeKeyState.class, PacketChangeKeyState::encode, PacketChangeKeyState::decode, PacketChangeKeyState.Handler::handle);
		HANDLER.registerMessage(discriminatorNumber++, PacketItemModifcation.class, PacketItemModifcation::encode, PacketItemModifcation::decode, PacketItemModifcation.Handler::handle);
	}
    
    
    public static final void sendToServer(Object msg)
    {
    	HANDLER.sendToServer(msg);
    }
    
    public static final void sendToPlayersTrackingEntity(Object msg, Entity entity)
    {
    	Stream<ServerPlayerEntity> players = ((ServerWorld)entity.world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ())), false);
		
    	players.forEach(player -> HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
    }


	public static final void sendToAll(Object msg) {
		
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		players.forEach(player -> HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
	}

	public static final void sendToPlayer(Object msg, PlayerEntity player) {
		HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static final void sendToDispatcher(Object msg, NetworkManager netman) {
		HANDLER.sendTo(msg, netman, NetworkDirection.PLAY_TO_CLIENT);
	}

	@Deprecated
	public static final void sendToNearby(Object msg, int dimId, int x, int y, int z, double dist) {
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionId(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public static final void sendToNearby(Object msg, ResourceLocation dimId, int x, int y, int z, double dist) {
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionIdentifier(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public static final void sendToNearby(Object msg, World world, int x, int y, int z, double dist) {
		ResourceLocation dimId = ZUtils.getDimensionIdentifier(world);
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionIdentifier(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	public static final void sendToNearby(Object packet,int dimId, BlockPos pos, double dist) {
		sendToNearby(packet, dimId, pos.getX(), pos.getY(), pos.getZ(), dist);
	}

}
