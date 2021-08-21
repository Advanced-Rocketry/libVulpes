package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SlotOreDict extends Slot {
	
	ResourceLocation acceptedNames;
	
	public SlotOreDict(IInventory par1iInventory, int par2, int par3, int par4, ResourceLocation allowedNames) {
		super(par1iInventory, par2, par3, par4);
		acceptedNames = allowedNames;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return ItemTags.getCollection().getOwningTags(stack.getItem()).contains(acceptedNames);
	}
}
