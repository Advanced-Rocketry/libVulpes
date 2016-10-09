package zmaster587.libVulpes.cap;

import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgePowerCapability implements IEnergyStorage {

	IUniversalEnergy energy;
	
	public ForgePowerCapability(IUniversalEnergy energy) {
		this.energy = energy;
	}
	
	@Override
	public int receiveEnergy(int paramInt, boolean paramBoolean) {
		return energy.acceptEnergy(paramInt, paramBoolean);
	}

	@Override
	public int extractEnergy(int paramInt, boolean paramBoolean) {
		return energy.extractEnergy(paramInt, paramBoolean);
		
	}

	@Override
	public int getEnergyStored() {
		return energy.getEnergyStored();
		
	}

	@Override
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return energy.canExtract();
	}

	@Override
	public boolean canReceive() {
		return energy.canReceive();
	}

}
