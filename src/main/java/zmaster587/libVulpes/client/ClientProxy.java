package zmaster587.libVulpes.client;

import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.common.CommonProxy;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	@Override
	public String getLocalizedString(String str) {
		return I18n.format(str);
	}
	
	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(LibVulpesItems.itemHoloProjector);
	}
}
