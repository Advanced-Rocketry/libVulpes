package zmaster587.libVulpes.tile.multiblock.hatch;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
public class TileInputHatch extends TileInventoryHatch   {



	public TileInputHatch() {
		super(4);
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
		return "block.libvulpes.itemihatch";
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
		return false;
	}
}
