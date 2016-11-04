package zmaster587.libVulpes.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import java.lang.reflect.Method;
import java.util.EnumMap;

import org.apache.logging.log4j.Level;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.BasePacket.BasePacketHandlerClient;
import zmaster587.libVulpes.network.BasePacket.BasePacketHandlerServer;
import zmaster587.libVulpes.network.BasePacket.BasePacketHandlerClient.executor;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHandler {
	
	private static Class<?> defaultChannelPipeline;
	private static int discriminatorNumber = 0;
	static Codec codec = new Codec();
	public static EnumMap<Side, FMLEmbeddedChannel> channels; //= NetworkRegistry.INSTANCE.newChannel("libVulpes", codec);
	
	public static PacketHandler INSTANCE = new PacketHandler();
	
	public PacketHandler() {
		codec = new Codec();
		channels = NetworkRegistry.INSTANCE.newChannel("libVulpes", codec);
	}

	public static void init() {
		if (!channels.isEmpty()) // avoid duplicate inits..
			return;
	}
	
    private static Method generateName;
    {
        try
        {
            defaultChannelPipeline = Class.forName("io.netty.channel.DefaultChannelPipeline");
            generateName = defaultChannelPipeline.getDeclaredMethod("generateName", ChannelHandler.class);
            generateName.setAccessible(true);
        }
        catch (Exception e)
        {
            // How is this possible?
            FMLLog.log(Level.FATAL, e, "What? Netty isn't installed, what magic is this?");
            throw Throwables.propagate(e);
        }
    }

	public final void addDiscriminator(Class clazz) {

		codec.addDiscriminator(discriminatorNumber, clazz);
		discriminatorNumber++;

		if(FMLCommonHandler.instance().getSide().isClient()) {
			FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
			String type = channel.findChannelHandlerNameForType(Codec.class);
			addClientHandlerAfter(channel, type, new BasePacketHandlerClient(), clazz);
		}
		//else {
			FMLEmbeddedChannel channel = channels.get(Side.SERVER);
			String type = channel.findChannelHandlerNameForType(Codec.class);
			addServerHandlerAfter(channels.get(Side.SERVER), type, new BasePacketHandlerServer(), clazz);
		//}


		//if(FMLCommonHandler.instance().getSide().isClient())
		//	INSTANCE.registerMessage(BasePacketHandlerClient.class, clazz, discriminatorNumber++, Side.CLIENT);
		//INSTANCE.registerMessage(BasePacketHandlerServer.class, clazz, discriminatorNumber++, Side.SERVER);

		/*if(codec != null) {
			codec.addDiscriminator(discriminatorNumber, clazz);
			discriminatorNumber++;
		}
		else
			LibVulpes.logger.warn("Trying to register " + clazz.getName() + " after preinit!!");*/
	}

    
	private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addServerHandlerAfter(FMLEmbeddedChannel channel, String type, IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestType)
    {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.SERVER, requestType);
        channel.pipeline().addAfter(type, generateName(channel.pipeline(), handler), handler);
    }

    private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addClientHandlerAfter(FMLEmbeddedChannel channel, String type, IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestType)
    {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.CLIENT, requestType);
        channel.pipeline().addAfter(type, generateName(channel.pipeline(), handler), handler);
    }

    private <REPLY extends IMessage, REQ extends IMessage> SimpleChannelHandlerWrapper<REQ, REPLY> getHandlerWrapper(IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Side side, Class<REQ> requestType)
    {
        return new SimpleChannelHandlerWrapper<REQ, REPLY>(messageHandler, side, requestType);
    }
    
    private String generateName(ChannelPipeline pipeline, ChannelHandler handler)
    {
        try
        {
            return (String)generateName.invoke(defaultChannelPipeline.cast(pipeline), handler);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.FATAL, e, "It appears we somehow have a not-standard pipeline. Huh");
            throw Throwables.propagate(e);
        }
    }
    

	public static final void sendToServer(BasePacket packet) {
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}


	public static final void sendToPlayersTrackingEntity(BasePacket packet, Entity entity) {
		for( EntityPlayer player : ((WorldServer)entity.worldObj).getEntityTracker().getTrackingPlayers(entity)) {

			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
			channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		}
	}

	public static final void sendToAll(BasePacket packet) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static final void sendToPlayer(BasePacket packet, EntityPlayer player) {
		//INSTANCE.sendTo(packet, (EntityPlayerMP)player);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

	}

	public static final void sendToDispatcher(BasePacket packet, NetworkManager netman) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(NetworkDispatcher.get(netman));
		channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static final void sendToNearby(BasePacket packet,int dimId, int x, int y, int z, double dist) {
		//INSTANCE.sendToAllAround(packet, new TargetPoint(dimId, x, y, z, dist));
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(dimId, x, y, z,dist));
		channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static final void sendToNearby(BasePacket packet,int dimId, BlockPos pos, double dist) {
		sendToNearby(packet, dimId, pos.getX(), pos.getY(), pos.getZ(), dist);
	}

	private static final class Codec extends FMLIndexedMessageToMessageCodec<BasePacket> {

		@Override
		public void encodeInto(ChannelHandlerContext ctx, BasePacket msg,
				ByteBuf data) throws Exception {
			msg.write(data);
		}

		@Override
		public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, BasePacket packet) {

			Side side = FMLCommonHandler.instance().getSide();
			if(FMLCommonHandler.instance().getSide().isClient()) {
				side = FMLCommonHandler.instance().getEffectiveSide();
			}

			switch (side) {
			case CLIENT:
				packet.readClient(data);
				LibVulpes.proxy.addScheduledTask(packet);
				break;
			case SERVER:
				//INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				packet.read(data);
				
				//NetHandlerPlayServer  net = (NetHandlerPlayServer)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				//((WorldServer)net.playerEntity.worldObj).addScheduledTask(new executorServer(packet, net.playerEntity, Side.SERVER));
				//packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
				break;
			}

		}
		
		public class executorServer implements Runnable {

			final Side side;
			final BasePacket packet;
			final EntityPlayer player;

			public executorServer(BasePacket packet, EntityPlayer player, Side side) {
				this.packet = packet;
				this.player = player;
				this.side = side;
			}

			@Override
			public void run() {
					packet.executeServer((EntityPlayerMP) player);
			}
		}
	}

	@Sharable
	@SideOnly(Side.CLIENT)
	private static final class HandlerClient extends SimpleChannelInboundHandler<BasePacket>
	{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, BasePacket packet) throws Exception
		{
			Minecraft mc = Minecraft.getMinecraft();
			packet.executeClient(mc.thePlayer); //actionClient(mc.theWorld, );
		}
	}
	@Sharable
	private static final class HandlerServer extends SimpleChannelInboundHandler<BasePacket>
	{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, BasePacket packet) throws Exception
		{
			if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			{
				// nothing on the client thread
				return;
			}
			EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
			packet.executeServer(player); //(player.worldObj, player);
		}
	}

}
