package zmaster587.libVulpes.cap;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import zmaster587.libVulpes.api.IUniversalEnergy;

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
