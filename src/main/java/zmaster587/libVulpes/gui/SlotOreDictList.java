package zmaster587.libVulpes.gui;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class SlotOreDictList extends Slot {

	Set<ResourceLocation> allowed;

	public SlotOreDictList(IInventory inv, int slot, int x, int y, Set<ResourceLocation> set) {
		super(inv, slot, x, y);
		allowed = set;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		for(ResourceLocation acceptedNames : allowed) {
			
			if(ItemTags.getCollection().getOwningTags(stack.getItem()).contains(acceptedNames))
				return true;
		}
		return false;
	}
}
