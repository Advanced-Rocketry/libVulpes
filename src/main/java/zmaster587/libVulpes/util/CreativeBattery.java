package zmaster587.libVulpes.util;

public class CreativeBattery extends UniversalBattery {
	@Override
	public int getEnergyStored() {
		return getMaxEnergyStored();
	}
	
	@Override
	public int extractEnergy(int maxExtract,
			boolean simulate) {
		return getMaxEnergyStored();
	}
	@Override
	public int getMaxEnergyStored() {
		return Short.MAX_VALUE;
	}
}
