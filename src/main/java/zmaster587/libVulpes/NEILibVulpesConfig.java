package zmaster587.libVulpes;

import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEILibVulpesConfig implements IConfigureNEI {


	@Override
	public String getName() {
		return "LibVulpesConfig";
	}

	@Override
	public String getVersion() {
		return "0.0.1";
	}

	@Override
	public void loadConfig() {
		//Hide stuff from NEI
		API.hideItem(new ItemStack(LibVulpesBlocks.blockPlaceHolder));
		API.hideItem(new ItemStack(LibVulpesBlocks.blockPhantom));
		
	}

}
