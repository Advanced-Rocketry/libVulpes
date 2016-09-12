package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileInputHatch extends TileInventoryHatch   {



	public TileInputHatch() {
		super();
	}

	public TileInputHatch(int size) {
		super(size);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.hatch.0.name";
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return false;
	}
}
