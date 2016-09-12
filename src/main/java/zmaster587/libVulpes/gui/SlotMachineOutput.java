package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class SlotMachineOutput extends SlotFurnaceOutput {
	
	public SlotMachineOutput(IInventory inv, int slot, int x, int y) {
		super(null,inv,slot,x,y);
	}
	
	//TODO: somthing here
	@Override
	protected void onCrafting(ItemStack par1ItemStack) {}
}
