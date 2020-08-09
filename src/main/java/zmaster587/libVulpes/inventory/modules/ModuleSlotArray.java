package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.TextureResources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class ModuleSlotArray extends ModuleBase {

	int startSlot, endSlot;
	IInventory inventory;
	ContainerModular container; //TODO: fix this OOPs (Object Oriented Poops)

	public ModuleSlotArray(int offsetX, int offsetY, IInventory container, int startSlot, int endSlot) {
		super(offsetX, offsetY);
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.inventory = container;
	}

	public void setSlotBounds(int a, int b) {

		for(int i = startSlot; i < endSlot; i++) {
			container.inventorySlots.remove(i);
			container.getInventory().remove(i);
		}
		startSlot = a;
		endSlot = b;
		
		for(Slot slot : getSlots(container)) {
			container.addSlotToContainer(slot);
		}
	}
	
	@Override
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		
		if(!isEnabled()) {
			gui.getMinecraft().getTextureManager().bindTexture(TextureResources.slotDisabled.getResourceLocation());
			for(Slot slot : slotList) {
				gui.func_238474_b_(mat, x + slot.xPos - 1, y + slot.yPos - 1, TextureResources.slotDisabled.getxLoc(), TextureResources.slotDisabled.getyLoc(), TextureResources.slotDisabled.getxSize(), TextureResources.slotDisabled.getySize());
			}
		}
	}

	@Override
	public List<Slot> getSlots(Container container) {	

		//Get the reference to the container
		this.container = (ContainerModular)container;

		for(int i = 0; i + startSlot < endSlot; i++) {
			Slot slot;
			slot = new Slot(inventory, i+startSlot, offsetX + 18* (i % 9), offsetY + 18*(i/9));

			slotList.add(slot);
		}
		return slotList;
	}
}
