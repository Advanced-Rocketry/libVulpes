package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class TileInputHatch extends TileInventoryHatch   {



	public TileInputHatch() {
		super(4);
	}

	public TileInputHatch(int size) {
		super(size);
	}

	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.itemihatch";
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
		return false;
	}
}
