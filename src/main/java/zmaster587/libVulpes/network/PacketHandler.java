package zmaster587.libVulpes.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zmaster587.libVulpes.network.PacketHandler.EncapsulatingPacket;
import zmaster587.libVulpes.util.ZUtils;

public class PacketHandler {

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

	static HashMap<Integer, Class<? extends BasePacket>> packetList = new HashMap<Integer, Class<? extends BasePacket>>();
	static HashMap<Class<? extends BasePacket>, Integer> revPacketList = new HashMap<Class<? extends BasePacket>, Integer>();
	public final void addDiscriminator(Class<? extends BasePacket> clazz) {
		packetList.put(clazz.getName().hashCode(), clazz);
		revPacketList.put(clazz, clazz.getName().hashCode());
	}


	public static final void register() {
		HANDLER.registerMessage(0, EncapsulatingPacket.class, EncapsulatingPacket::encode, EncapsulatingPacket::decode, EncapsulatingPacket.Handler::handle);
		INSTANCE.addDiscriminator(PacketChangeKeyState.class);
		INSTANCE.addDiscriminator(PacketEntity.class);
		INSTANCE.addDiscriminator(PacketItemModifcation.class);
		INSTANCE.addDiscriminator(PacketMachine.class);
		INSTANCE.addDiscriminator(PacketSpawnEntity.class);
	}
	
	
	public static class EncapsulatingPacket
	{

		BasePacket basePacket;
		
		EncapsulatingPacket(BasePacket pkt)
		{
			basePacket = pkt;
		}
		
		public static void encode(EncapsulatingPacket pkt, PacketBuffer buf)
		{
			buf.writeInt(PacketHandler.getIdByClass(pkt.basePacket.getClass()));
			pkt.basePacket.write(buf);
		}

		public static EncapsulatingPacket decode( PacketBuffer buf)
		{
			int index = buf.readInt();

			Class<? extends BasePacket> clazz = PacketHandler.getClassById(index);

			BasePacket pkt;
			try {
				pkt = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			
			if(EffectiveSide.get() == LogicalSide.CLIENT)
				pkt.readClient(buf);
			else
				pkt.read(buf);

			return new EncapsulatingPacket(pkt);
		}

		public static class Handler
		{
			
			@OnlyIn(Dist.CLIENT)
			private static void CallClient(EncapsulatingPacket msg, Supplier<NetworkEvent.Context> ctx)
			{
				ctx.get().enqueueWork(() -> msg.basePacket.executeClient(Minecraft.getInstance().player));
			}
			
			public static void handle(EncapsulatingPacket msg, Supplier<NetworkEvent.Context> ctx)
			{
				if(EffectiveSide.get() == LogicalSide.CLIENT)
					CallClient(msg, ctx);
				else
					ctx.get().enqueueWork(() -> msg.basePacket.executeServer(ctx.get().getSender()));

				ctx.get().setPacketHandled(true);

			}
		}
	}
	
	public static Class<? extends BasePacket> getClassById(int id)
	{
		return packetList.get(id);
	}
	public static int getIdByClass(Class<? extends BasePacket> id)
	{
		return revPacketList.get(id);
	}
    
    public static final void sendToServer(BasePacket pkt)
    {
    	Object msg = new EncapsulatingPacket(pkt);
    	HANDLER.sendToServer(msg);
    }
    
    public static final void sendToPlayersTrackingEntity(BasePacket pkt, Entity entity)
    {
    	Object msg = new EncapsulatingPacket(pkt);
    	Stream<ServerPlayerEntity> players = ((ServerWorld)entity.world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ())), false);
		
    	players.forEach(player -> HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
    }


	public static final void sendToAll(BasePacket pkt) {
		Object msg = new EncapsulatingPacket(pkt);
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		players.forEach(player -> HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
	}

	public static final void sendToPlayer(BasePacket pkt, PlayerEntity player) {
		Object msg = new EncapsulatingPacket(pkt);
		HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static final void sendToDispatcher(BasePacket pkt, NetworkManager netman) {
		Object msg = new EncapsulatingPacket(pkt);
		HANDLER.sendTo(msg, netman, NetworkDirection.PLAY_TO_CLIENT);
	}

	@Deprecated
	public static final void sendToNearby(BasePacket pkt, int dimId, int x, int y, int z, double dist) {
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		Object msg = new EncapsulatingPacket(pkt);
		
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionId(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public static final void sendToNearby(BasePacket pkt, ResourceLocation dimId, BlockPos pos, double dist) {
		sendToNearby(pkt, dimId, pos.getX(), pos.getY(), pos.getZ(), dist);
	}
	
	public static final void sendToNearby(BasePacket pkt, ResourceLocation dimId, int x, int y, int z, double dist) {
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		Object msg = new EncapsulatingPacket(pkt);
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionIdentifier(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public static final void sendToNearby(BasePacket pkt, World world, int x, int y, int z, double dist) {
		ResourceLocation dimId = ZUtils.getDimensionIdentifier(world);
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		Object msg = new EncapsulatingPacket(pkt);
		
		for(ServerPlayerEntity player : players)
		{
			if(ZUtils.getDimensionIdentifier(player.getEntityWorld()) == dimId && player.getDistanceSq(x, y, z) <= dist*dist)
				HANDLER.sendTo(msg, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@Deprecated
	public static final void sendToNearby(BasePacket pkt,int dimId, BlockPos pos, double dist) {
		sendToNearby(pkt, dimId, pos.getX(), pos.getY(), pos.getZ(), dist);
	}
	
	public static final void sendToNearby(BasePacket pkt, World dimId, BlockPos pos, double dist) {
		sendToNearby(pkt, dimId, pos.getX(), pos.getY(), pos.getZ(), dist);
	}

}
