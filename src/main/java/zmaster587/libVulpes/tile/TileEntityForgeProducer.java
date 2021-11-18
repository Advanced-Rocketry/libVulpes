package zmaster587.libVulpes.tile;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.util.UniversalBattery;

public abstract class TileEntityForgeProducer extends TileEntity implements IModularInventory, IEnergyStorage,  IPower, IUniversalEnergy, ITickableTileEntity {
	protected UniversalBattery energy;

	protected TileEntityForgeProducer(TileEntityType<?> tileEntityTypeIn, int energy) {
		super(tileEntityTypeIn);
		this.energy = new UniversalBattery(energy);
	}


	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		LinkedList<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModulePower(18, 20, energy));

		return modules;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if(capability == CapabilityEnergy.ENERGY )
			return LazyOptional.of(() -> new ForgePowerCapability(this)).cast();
		/*else if(TeslaHandler.hasTeslaCapability(this, capability))
			return LazyOptional.of(() -> TeslaHandler.getHandler(this)).cast();*/

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
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
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

	public boolean hasEnoughEnergyBuffer(int amt) {
		return getMaxEnergyStored() - getUniversalEnergyStored() >= amt;
	}

	public int getPowerPerOperation() {
		return 0;
	}

	public abstract boolean canGeneratePower();

	@Override
	public void tick() {

		if(canGeneratePower()) {

			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!world.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
			}
			else
				notEnoughBufferForFunction();
		}
		if(!world.isRemote)
			transmitPower();

	}

	protected void transmitPower() {
		for(Direction facing : Direction.values()) {
			TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

			if(tile != null ) {
				IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).orElse(null);
				if(storage != null && storage.canReceive())
					this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
			}
		}
	}

	public abstract void onGeneratePower();

	public void notEnoughBufferForFunction() {

	}

	@Override
	public int getEnergyStored(Direction from) {
		return energy.getUniversalEnergyStored();
	}
	
	@Override
	public int getEnergyStored() {
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
		return energy.extractEnergy(amt, simulate);
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
