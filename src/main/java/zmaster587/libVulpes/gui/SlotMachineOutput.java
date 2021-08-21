package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.FurnaceResultSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotMachineOutput extends FurnaceResultSlot {
	
	public SlotMachineOutput(IInventory inv, int slot, int x, int y) {
		super(null,inv,slot,x,y); // TODO: we can't pass null here
	}
	
	//TODO: something here
	@Override
	protected void onCrafting(@Nonnull ItemStack par1ItemStack) {}
}
