package zmaster587.libVulpes.cap;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidCapability implements IFluidHandler{

	IFluidHandler tile;
	
	public FluidCapability(IFluidHandler tile) {
		this.tile = tile;
	}
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tile.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return tile.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return tile.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tile.drain(maxDrain, doDrain);
	}

}
