package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.util.IconResource;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

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
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		for(Slot slot : slotList) {
			gui.blit(mat, x + slot.xPos - 1, y + slot.yPos - 1, iconResource.getxLoc(), iconResource.getyLoc(), iconResource.getxSize(), iconResource.getySize());
		}
	}
}
