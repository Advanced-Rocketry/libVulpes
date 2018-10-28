package zmaster587.libVulpes.tile.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class TileForgePowerInput extends TilePlugBase implements IEnergyStorage {

	@Override
	public String getModularInventoryName() {
		return "tile.forgePowerInput.name";
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return acceptEnergy(amt, simulate);
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public int getEnergyStored() {
		return getUniversalEnergyStored();
	}
	
	@Override
	public boolean canReceive() {
		return true;
	}

}
