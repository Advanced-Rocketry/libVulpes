package zmaster587.libVulpes.tile.multiblock.hatch;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleOutputSlotArray;

public class TileOutputHatch extends TileInventoryHatch {

	

	public TileOutputHatch() {
		super();
	}

	public TileOutputHatch(int size) {
		super(size);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.hatch.1.name";
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return false;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleOutputSlotArray(8, 18, this, 0, this.getSizeInventory()));
		return modules;
	}
	
	
}
