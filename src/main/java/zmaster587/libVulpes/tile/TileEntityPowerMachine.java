package zmaster587.libVulpes.tile;

import cofh.api.energy.IEnergyHandler;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
public abstract class TileEntityPowerMachine extends TileEntityMachine implements IEnergyHandler, IUniversalEnergy {

	protected UniversalBattery energy;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energy.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energy.readFromNBT(nbt);
	}
	
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		return energy.acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}
	
	public boolean hasEnergy() { return energy.getEnergyStored() > 0; }

	@Override
	public void setEnergyStored(int value) {
		energy.setEnergyStored(value);
	}

	
	public void removePower(int amt) {
		energy.extractEnergy(amt, false);
	}
	
	
	public int getPower() {
		return energy.getEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}
}
