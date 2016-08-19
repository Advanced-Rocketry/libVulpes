package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.item.ItemStack;

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
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return false;
	}
}
