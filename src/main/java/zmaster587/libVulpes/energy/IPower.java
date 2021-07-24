package zmaster587.libVulpes.energy;

import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.util.Direction;

public interface IPower extends IUniversalEnergy {

	boolean canConnectEnergy(Direction facing);
	
	int extractEnergy(Direction dir, int maxExtract, boolean simulate);
	
	int getEnergyStored(Direction dir);
	
	int getMaxEnergyStored(Direction dir);
	
	int receiveEnergy(Direction dir, int amt, boolean simulate);

	/**
	 * @param amt
	 * @param simulate
	 * @return amount of energy used out of amt
	 */
	int receiveEnergy(int amt, boolean simulate);
}
