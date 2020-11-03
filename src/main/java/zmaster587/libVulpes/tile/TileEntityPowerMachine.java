package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public abstract class TileEntityPowerMachine extends TileEntityMachine implements IPower, IUniversalEnergy {

	public TileEntityPowerMachine(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	protected UniversalBattery energy;
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		energy.write(nbt);
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return LazyOptional.of(() -> new ForgePowerCapability(this)).cast();
		
		return super.getCapability(capability, facing);
	}
	
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		energy.readFromNBT(nbt);
	}
	
	@Override
	public int receiveEnergy(Direction from, int maxReceive,
			boolean simulate) {
		return energy.acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(Direction from, int maxExtract,
			boolean simulate) {
		return 0;
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

	@Override
	public void setEnergyStored(int value) {
		energy.setEnergyStored(value);
	}

	
	public void removePower(int amt) {
		energy.extractEnergy(amt, false);
	}
	
	
	public int getPower() {
		return energy.getUniversalEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
	}
}
