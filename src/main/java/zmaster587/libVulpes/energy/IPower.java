package zmaster587.libVulpes.energy;

import net.minecraft.util.EnumFacing;

public interface IPower {
	
	public boolean canConnectEnergy(EnumFacing facing);
	
	public int extractEnergy(EnumFacing dir, int maxExtract, boolean simulate);
	
	public int getEnergyStored(EnumFacing dir);
	
	public int getMaxEnergyStored(EnumFacing dir);
	
	public int receiveEnergy(EnumFacing dir, int amt, boolean simulate);
	
	public int receiveEnergy(int amt, boolean simulate);
}
