package zmaster587.libVulpes.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidHandlerInternal extends IFluidHandler {
	public int fillInternal(FluidStack resource, boolean doDrain);
	
	public FluidStack drainInternal(int maxDrain, boolean doDrain);
	
	public FluidStack drainInternal(FluidStack maxDrain, boolean doDrain);
}
