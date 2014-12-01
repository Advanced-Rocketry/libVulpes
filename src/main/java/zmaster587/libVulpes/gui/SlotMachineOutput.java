package zmaster587.libVulpes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class SlotMachineOutput extends SlotFurnace {
	
	public SlotMachineOutput(EntityPlayer player,IInventory inv, int slot, int x, int y) {
		super(player,inv,slot,x,y);
	}
	
	//TODO: somthing here
	@Override
	protected void onCrafting(ItemStack par1ItemStack) {}
}
