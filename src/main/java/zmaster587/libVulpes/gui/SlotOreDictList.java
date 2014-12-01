package zmaster587.libVulpes.gui;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SlotOreDictList extends Slot {

	Set<String> allowed;

	public SlotOreDictList(IInventory inv, int slot, int x, int y, Set<String> set) {
		super(inv, slot, x, y);
		allowed = set;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		int stackId = OreDictionary.getOreID(stack);
		if(stackId == -1)
			return false;
		
		for(String str : allowed) {
			if(OreDictionary.getOreName(stackId).contains(str))
				return true;
		}
		
		return false;
	}
}
