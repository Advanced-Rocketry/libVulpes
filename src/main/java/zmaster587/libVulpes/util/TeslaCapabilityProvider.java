package zmaster587.libVulpes.util;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;
import zmaster587.libVulpes.tile.energy.TilePlugBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TeslaCapabilityProvider {

	public static void registerCap() {
	    //MinecraftForge.EVENT_BUS.register(TeslaCapabilityProvider.class);
	    LibVulpes.logger.info("Tesla integration loaded");
	}
	private static final ResourceLocation KEY = new ResourceLocation("libvulpes:ProviderTesla");

//	@SubscribeEvent
//	public static void attachCapabilities(AttachCapabilitiesEvent.TileEntity evt)
//	{
//		if (evt.getCapabilities().containsKey(KEY)) {
//			return;
//		}
//		TileEntity te = evt.getTileEntity();
//		if (te instanceof TileEntityRFConsumer || te instanceof TilePlugBase) {
//			evt.addCapability(KEY, te);
//		}
//	}

}
