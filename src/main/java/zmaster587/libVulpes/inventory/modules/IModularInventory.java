package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;

public interface IModularInventory extends INamedContainerProvider {
	
	/**
	 * @return a list of modules to add to the inventory
	 */
	public List<ModuleBase> getModules(int id, PlayerEntity player);
	
	public String getModularInventoryName();
	
	public boolean canInteractWithContainer(PlayerEntity entity);
	
	public int getModularInvType();
	
}
