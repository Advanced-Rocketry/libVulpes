package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import zmaster587.libVulpes.inventory.GuiHandler;

public interface IModularInventory extends INamedContainerProvider {
	
	/**
	 * @return a list of modules to add to the inventory
	 */
	List<ModuleBase> getModules(int id, PlayerEntity player);
	
	String getModularInventoryName();

	boolean canInteractWithContainer(PlayerEntity entity);
	
	GuiHandler.guiId getModularInvType();
	
}
