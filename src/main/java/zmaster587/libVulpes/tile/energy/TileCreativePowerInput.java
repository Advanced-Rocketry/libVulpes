package zmaster587.libVulpes.tile.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.CreativeBattery;
import zmaster587.libVulpes.util.UniversalBattery;

public class TileCreativePowerInput extends TilePlugBase implements IPower {

	public TileCreativePowerInput() {
		//super(1);
		storage = new CreativeBattery();
	}

	public TileCreativePowerInput(int teir) {
		//super(1);
		storage = new CreativeBattery();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		teir = nbt.getInteger("teir");
		
		storage = new CreativeBattery();//new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}
	
	@Override
	public int extractEnergy(EnumFacing dir, int amt, boolean sim) {
		return extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(EnumFacing arg0) {
		return getEnergyStored();//getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing arg0) {
		return getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(EnumFacing arg0, int maxReceive, boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing arg0) {
		return true;
	}


	@Override
	public String getModularInventoryName() {
		return "tile.creativePowerBattery.name";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return receiveEnergy(amt, simulate);
	}

	@Override
	public boolean canReceive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}


}
