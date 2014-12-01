package zmaster587.libVulpes.gui;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLimitedItem extends Slot {
	
	IlimitedItemSlotEntity tile;
	
	public SlotLimitedItem(IInventory inventory, int slotIndex, int x, int y, IlimitedItemSlotEntity entity) {
		super(inventory, slotIndex, x, y);
		this.tile = entity;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return tile.isItemValidForLimitedSlot(this.slotNumber, stack);
	}
}
