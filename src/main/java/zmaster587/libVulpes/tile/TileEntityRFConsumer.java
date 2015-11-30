package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.util.MultiBattery;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

public abstract class TileEntityRFConsumer extends TileEntity implements IEnergyHandler, IUniversalEnergy {
	protected UniversalBattery energy;

	protected TileEntityRFConsumer(int energy) {
		this.energy = new UniversalBattery(energy);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}
	
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
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return extractEnergy(maxExtract, simulate);
	}

	public boolean hasEnoughEnergy(int amt) {
		return getEnergyStored() >= amt;
	}

	public int getPowerPerOperation() {
		return 0;
	}

	public abstract boolean canPerformFunction();


	@Override
	public void updateEntity() {
		super.updateEntity();

		if(canPerformFunction()) {

			if(hasEnoughEnergy(getPowerPerOperation())) {
				if(!worldObj.isRemote) this.energy.extractEnergy(getPowerPerOperation(), false);
				performFunction();
			}
			else
				notEnoughEnergyForFunction();
		}
	}

	public abstract void performFunction();

	public void notEnoughEnergyForFunction() {

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

	public void setEnergyStored(int value) {
		energy.setEnergyStored(value);
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return energy.extractEnergy(amt, false);
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
	public int acceptEnergy(int amt, boolean simulate) {
		return energy.acceptEnergy(amt, simulate);
	}
}
