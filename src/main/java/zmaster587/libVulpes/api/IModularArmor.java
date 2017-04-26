package zmaster587.libVulpes.api;

import java.util.List;

import zmaster587.libVulpes.util.IconResource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IModularArmor {
	
	public void addArmorComponent(World world, ItemStack armor, ItemStack componentStack, int slot);
	
	public ItemStack removeComponent(World world, ItemStack armor, int index);
	
	public List<ItemStack> getComponents(ItemStack armor);

	public ItemStack getComponentInSlot(ItemStack stack, int slot);
	
	//Returns a list of externally modifiable fluidtanks
	public boolean canBeExternallyModified(ItemStack armor, int slot);
	
	public int getNumSlots(ItemStack stack);
	
	public IInventory loadModuleInventory(ItemStack stack);
	
	public void saveModuleInventory(ItemStack stack, IInventory inv);
	
	//returns true if the stack is valid for the given slot
	public boolean isItemValidForSlot(ItemStack stack, int slot);
	
	//Returns an IconResource to be displayed in the slot, if null default slot texture is used
	public IconResource getResourceForSlot(int slot);
}
