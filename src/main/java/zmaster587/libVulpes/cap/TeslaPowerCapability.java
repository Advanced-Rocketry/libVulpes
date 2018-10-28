package zmaster587.libVulpes.cap;

import zmaster587.libVulpes.api.IUniversalEnergy;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

public class TeslaPowerCapability implements ITeslaConsumer, ITeslaProducer, ITeslaHolder {
	
	IUniversalEnergy energy;
	
	public TeslaPowerCapability(IUniversalEnergy energy) {
		this.energy = energy;
	}

	@Override
	public long getCapacity() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public long getStoredPower() {
		return energy.getUniversalEnergyStored();
	}

	@Override
	public long takePower(long amt, boolean simulate) {
		return energy.extractEnergy((int)amt, simulate);
	}

	@Override
	public long givePower(long amt, boolean simulate) {
		return energy.acceptEnergy((int)amt, simulate);
	}
}
