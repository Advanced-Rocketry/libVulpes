package zmaster587.libVulpes.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IJetPack {
	public boolean isActive(ItemStack stack, PlayerEntity player);
	
	public boolean isEnabled(ItemStack stack);
	
	public void setEnabledState(ItemStack stack, boolean state);
	
	public void onAccelerate(ItemStack stack, IInventory inv, PlayerEntity player);
	
	public void changeMode(ItemStack stack, IInventory modules, PlayerEntity player);
}
