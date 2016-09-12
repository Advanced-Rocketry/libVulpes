package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public abstract class TileEntityRFConsumer extends TileEntity implements IPower, IUniversalEnergy, ITickable {
	protected UniversalBattery energy;

	protected TileEntityRFConsumer(int energy) {
		this.energy = new UniversalBattery(energy);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing arg0) {
		return true;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energy.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energy.readFromNBT(nbt);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive,
			boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract,
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
	public void update() {

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
	public int getEnergyStored(EnumFacing from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
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

	public void setMaxEnergyStored(int max) {
		energy.setEnergyStored(max);
	}
	
	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return energy.acceptEnergy(amt, simulate);
	}
}
