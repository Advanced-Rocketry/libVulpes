package zmaster587.libVulpes.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.config.LibVulpesConfig;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.UniversalBattery;

public abstract class TileEntityFEConsumer extends TileEntity implements IPower, IUniversalEnergy, ITickableTileEntity {
	protected UniversalBattery energy;

	protected TileEntityFEConsumer(TileEntityType<?> type, int energy) {
		super(type);
		this.energy = new UniversalBattery(energy);
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return LazyOptional.of( () -> new ForgePowerCapability(this)).cast();
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		energy.write(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		energy.readFromNBT(nbt);
	}

	@Override
	public int receiveEnergy(Direction from, int maxReceive,
			boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(Direction from, int maxExtract,
			boolean simulate) {
		return extractEnergy(maxExtract, simulate);
	}

	public boolean hasEnoughEnergy(int amt) {
		return getUniversalEnergyStored() >= amt;
	}

	public int getPowerPerOperation() {
		return 0;
	}

	public abstract boolean canPerformFunction();


	@Override
	public void tick() {

		if(canPerformFunction()) {

			if(hasEnoughEnergy(Math.max(getPowerPerOperation(), 1))) {
				if(!world.isRemote) this.energy.extractEnergy(getPowerPerOperation(), false);
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
	public int getEnergyStored(Direction from) {
		return energy.getUniversalEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(Direction from) {
		return energy.getMaxEnergyStored();
	}

	public boolean hasEnergy() { return energy.getUniversalEnergyStored() > 0; }

	public void setEnergyStored(int value) {
		energy.setEnergyStored(value);
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return energy.extractEnergy(amt, false);
	}

	@Override
	public int getUniversalEnergyStored() {
		return energy.getUniversalEnergyStored();
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

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return energy.acceptEnergy(amt, simulate);
	}
}
