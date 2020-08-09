package zmaster587.libVulpes.inventory.modules;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IButtonInventory {

	/**
	 * Called on the client when a user presses a button
	 * @param buttonId id of the button pressed
	 */
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(int buttonId);
	
}
