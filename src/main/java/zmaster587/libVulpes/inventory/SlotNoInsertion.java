package zmaster587.libVulpes.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;

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
