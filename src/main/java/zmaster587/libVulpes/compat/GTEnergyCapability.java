package zmaster587.libVulpes.compat;

import net.minecraft.util.EnumFacing;
import zmaster587.libVulpes.Configuration;
import zmaster587.libVulpes.util.UniversalBattery;

public class GTEnergyCapability implements gregtech.api.capability.IEnergyContainer {

	UniversalBattery storage;
	
	public GTEnergyCapability(UniversalBattery storage)
	{
		this.storage = storage;
	}
	
	@Override
	public long getEnergyStored() {
		return storage.getUniversalEnergyStored();
	}

	@Override
	public long acceptEnergyFromNetwork(EnumFacing arg0, long arg1, long arg2) {
		return storage.acceptEnergy((int)(arg1*arg2*Configuration.EUMult), false);
	}

	@Override
	public long changeEnergy(long arg0) {
		if (arg0 < 0)
			return storage.extractEnergy((int) -arg0, false);
		else 
			return storage.acceptEnergy((int) arg0, false);
	}

	@Override
	public long getEnergyCapacity() {
		// TODO Auto-generated method stub
		return storage.getMaxEnergyStored();
	}

	@Override
	public long getInputAmperage() {
		return 1;
	}

	@Override
	public long getInputVoltage() {
		return 32;
	}

	@Override
	public boolean inputsEnergy(EnumFacing arg0) {
		return true;
	}
}
