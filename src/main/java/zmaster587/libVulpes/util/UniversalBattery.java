package zmaster587.libVulpes.util;

import net.minecraft.nbt.NBTTagCompound;
import zmaster587.libVulpes.api.IUniversalEnergy;

public class UniversalBattery implements IUniversalEnergy {

	private int energy;
	private int maxEnergy;
	
	public UniversalBattery() {
		maxEnergy = 0;
	}
	
	public UniversalBattery(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}
	
	@Override
	public int acceptEnergy(int maxReceive,
			boolean simulate) {

		if(maxReceive > maxEnergy - energy) {
			int returnAmt = maxEnergy - energy;
			if(!simulate)
				energy = maxEnergy;
			
			return returnAmt;
		}
		
		if(!simulate)
			energy += maxReceive;
		
		return maxReceive;
	}

	@Override
	public int extractEnergy(int maxExtract,
			boolean simulate) {
		if(maxExtract > energy) {
			int ret = energy;
			if(!simulate)
				energy = 0;
			return ret;
		}
		
		if(!simulate)
			energy -= maxExtract;
		
		return maxExtract;
	}
	
	@Override
	public int getUniversalEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return maxEnergy;
	}
	
	@Override
	public void setMaxEnergyStored(int max) {
		maxEnergy = max;
	}

	@Override
	public void setEnergyStored(int energy) {
		this.energy = energy;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("energy", this.energy);
		nbt.setInteger("maxEnergy", this.maxEnergy);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		this.energy = nbt.getInteger("energy");
		this.maxEnergy = nbt.getInteger("maxEnergy");
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
}
