package zmaster587.libVulpes.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IJetPack {
	boolean isActive(ItemStack stack, PlayerEntity player);
	
	boolean isEnabled(@Nonnull ItemStack stack);
	
	void setEnabledState(@Nonnull ItemStack stack, boolean state);

	void onAccelerate(ItemStack stack, IInventory inv, PlayerEntity player);
	
	void changeMode(ItemStack stack, IInventory modules, PlayerEntity player);
}
