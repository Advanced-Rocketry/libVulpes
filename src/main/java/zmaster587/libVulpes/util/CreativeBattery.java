package zmaster587.libVulpes.util;

public class CreativeBattery extends UniversalBattery {
	@Override
	public int getUniversalEnergyStored() {
		return getMaxEnergyStored();
	}
	
	@Override
	public int extractEnergy(int maxExtract,
			boolean simulate) {
		return getMaxEnergyStored();
	}
	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE >> 4;
	}
}
