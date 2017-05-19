package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.util.IconResource;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ModuleTexturedSlotArray extends ModuleSlotArray {

	IconResource iconResource;
	
	public ModuleTexturedSlotArray(int offsetX, int offsetY, IInventory container, int startSlot, int endSlot, IconResource iconResource) {
		super(offsetX, offsetY, container, startSlot, endSlot);
		this.iconResource = iconResource;
	}
	
	public void setResource(IconResource iconResource) {
		this.iconResource = iconResource;
	}
	
	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		for(Slot slot : slotList) {
			gui.drawTexturedModalRect(x + slot.xPos - 1, y + slot.yPos - 1, iconResource.getxLoc(), iconResource.getyLoc(), iconResource.getxSize(), iconResource.getySize());
		}
	}
}
