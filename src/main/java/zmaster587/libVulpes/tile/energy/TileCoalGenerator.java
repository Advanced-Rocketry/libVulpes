package zmaster587.libVulpes.tile.energy;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

public class TileCoalGenerator extends TileInventoriedForgePowerMachine {

	int powerPerTick;
	private static final int INPUT_SLOT = 0;
	
	public TileCoalGenerator() {
		super(10000, 1);
		powerPerTick = 40;
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);
		
		modules.add(new ModuleSlotArray(40, 40, this, 0, 1));
		
		return modules;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		
		if(!canGeneratePower())
			setTimeAndAmounts();
	}
	
	@Override
	public int getPowerPerOperation() {
		return powerPerTick;
	}

	@Override
	protected void onOperationFinish() {
		super.onOperationFinish();
		setTimeAndAmounts();
	}

	private void setTimeAndAmounts() {
		timeRemaining = TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(INPUT_SLOT));
		inventory.decrStackSize(INPUT_SLOT, 1);
	}

	@Override
	public void onGeneratePower() {

	}

	@Override
	public String getModularInventoryName() {
		return "tile.coalGenerator.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}
}