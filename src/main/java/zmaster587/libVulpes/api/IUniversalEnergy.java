package zmaster587.libVulpes.api;


public interface IUniversalEnergy  {
	public void setEnergyStored(int amt);

	int extractEnergy(int amt, boolean simulate);

	int getEnergyStored();

	int getMaxEnergyStored();

	int acceptEnergy(int amt, boolean simulate);
	
	void setMaxEnergyStored(int max);

	public boolean canReceive();

	public boolean canExtract();
}
