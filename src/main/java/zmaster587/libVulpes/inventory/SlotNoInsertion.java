package zmaster587.libVulpes.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class SlotNoInsertion extends Slot {

	public SlotNoInsertion(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
