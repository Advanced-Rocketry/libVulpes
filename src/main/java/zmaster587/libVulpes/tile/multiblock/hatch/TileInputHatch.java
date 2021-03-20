package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileInputHatch extends TileInventoryHatch   {



	public TileInputHatch() {
		super();
	}

	public TileInputHatch(int size) {
		super(size);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanInsertSlot(1, true);
		inventory.setCanInsertSlot(2, true);
		inventory.setCanInsertSlot(3, true);
		inventory.setCanExtractSlot(0, false);
		inventory.setCanExtractSlot(1, false);
		inventory.setCanExtractSlot(2, false);
		inventory.setCanExtractSlot(3, false);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.hatch.0.name";
	}
}
