package zmaster587.libVulpes.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public interface IFluidHandlerInternal extends IFluidHandler {
	public int fillInternal(FluidStack resource, FluidAction doDrain);
	
	public FluidStack drainInternal(int maxDrain, FluidAction doDrain);
	
	public FluidStack drainInternal(FluidStack maxDrain, FluidAction doDrain);
}
