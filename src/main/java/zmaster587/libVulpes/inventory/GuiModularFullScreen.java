package zmaster587.libVulpes.inventory;

import java.util.List;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class GuiModularFullScreen extends GuiModular {

	public GuiModularFullScreen(EntityPlayer playerInv,
			List<ModuleBase> modules, IModularInventory modularInv,
			boolean includePlayerInv, boolean includeHotBar, String name) {
		super(playerInv, modules, modularInv, includePlayerInv,includeHotBar, name);
		
		this.xSize = Minecraft.getMinecraft().displayWidth;
		this.ySize = Minecraft.getMinecraft().displayHeight;
		this.width = Minecraft.getMinecraft().displayWidth;
		this.height = Minecraft.getMinecraft().displayHeight;
	}
	
	@Override
	public void initGui() {
		this.xSize = Minecraft.getMinecraft().displayWidth;
		this.ySize = Minecraft.getMinecraft().displayHeight;
		this.width = Minecraft.getMinecraft().displayWidth;
		this.height = Minecraft.getMinecraft().displayHeight;
		
		super.initGui();
	}
}
