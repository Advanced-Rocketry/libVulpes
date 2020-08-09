package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.util.IconResource;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
public class ModuleTexturedLimitedSlotArray extends ModuleLimitedSlotArray {

	IconResource iconResource;

	public ModuleTexturedLimitedSlotArray(int offsetX, int offsetY,
			IInventory container, int startSlot, int endSlot, IconResource resource) {
		super(offsetX, offsetY, container, startSlot, endSlot);
		this.iconResource = resource;
	}

	public void setResource(IconResource iconResource) {
		this.iconResource = iconResource;
	}
	
	@Override
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		
		if(iconResource != null) {
			if(iconResource.getResourceLocation() != null)
				gui.getMinecraft().getTextureManager().bindTexture(iconResource.getResourceLocation());
			for(Slot slot : slotList) {
				
				gui.func_238474_b_(mat, x + slot.xPos - 1, y + slot.yPos - 1, iconResource.getxLoc(), iconResource.getyLoc(), iconResource.getxSize(), iconResource.getySize());
			}
		}
		else
			super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
	}

}
