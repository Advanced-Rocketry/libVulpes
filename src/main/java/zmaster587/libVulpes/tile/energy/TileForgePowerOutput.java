package zmaster587.libVulpes.tile.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class TileForgePowerOutput extends TilePlugBase implements IEnergyStorage {

	public TileForgePowerOutput() {
		super(1);
	}
	
	@Override
	public String getModularInventoryName() {
		return "tile.forgePowerOutput.name";
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return acceptEnergy(amt, simulate);
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

}
