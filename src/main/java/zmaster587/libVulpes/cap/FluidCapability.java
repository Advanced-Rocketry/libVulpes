package zmaster587.libVulpes.cap;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidCapability implements IFluidHandler{

	IFluidHandler tile;
	
	public FluidCapability(IFluidHandler tile) {
		this.tile = tile;
	}

	@Override
	public int getTanks() {
		return tile.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return tile.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return tile.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return tile.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return tile.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return tile.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return tile.drain(maxDrain, action);
	}

}
