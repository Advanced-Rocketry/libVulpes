package zmaster587.libVulpes.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidHandlerInternal extends IFluidHandler {
	int fillInternal(FluidStack resource, FluidAction doDrain);
	
	FluidStack drainInternal(int maxDrain, FluidAction doDrain);
	
	FluidStack drainInternal(FluidStack maxDrain, FluidAction doDrain);
}
