package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotLimitedItem extends Slot {
	
	IInventory tile;
	
	public SlotLimitedItem(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.tile = inventory;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return tile.isItemValidForSlot(this.slotNumber, stack);
	}
}
