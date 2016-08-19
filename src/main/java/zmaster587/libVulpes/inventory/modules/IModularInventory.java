package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface IModularInventory {
	
	/**
	 * @return a list of modules to add to the inventory
	 */
	public List<ModuleBase> getModules(int id, EntityPlayer player);
	
	public String getModularInventoryName();
	
	public boolean canInteractWithContainer(EntityPlayer entity);
	
}
