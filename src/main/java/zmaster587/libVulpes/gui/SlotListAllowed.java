package zmaster587.libVulpes.gui;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotListAllowed extends Slot {

	Set<ItemStack> allowedItems;
	
	public SlotListAllowed(IInventory inventory, int slotIndex, int x, int y, Set<ItemStack> allowedItems) {
		super(inventory, slotIndex, x, y);
		this.allowedItems = allowedItems;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		for(ItemStack i : allowedItems){
			if(i.isItemEqual(stack))
				return true;
		}
		return false;
	}
}
