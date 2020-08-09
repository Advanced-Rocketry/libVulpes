package zmaster587.libVulpes.energy;

import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.util.Direction;

public interface IPower extends IUniversalEnergy {
	
	public boolean canConnectEnergy(Direction facing);
	
	public int extractEnergy(Direction dir, int maxExtract, boolean simulate);
	
	public int getEnergyStored(Direction dir);
	
	public int getMaxEnergyStored(Direction dir);
	
	public int receiveEnergy(Direction dir, int amt, boolean simulate);
	
	public int receiveEnergy(int amt, boolean simulate);
}
