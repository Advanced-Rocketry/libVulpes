package zmaster587.libVulpes.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidUtils {

	

	public static boolean containsFluid(ItemStack stack) {
		return stack != null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
	}
	
	public static boolean areFluidsSameType(Fluid in, Fluid otherFluid) {
		return in != null && otherFluid != null && in.getName().equals(otherFluid.getName());
	}
	
	public static FluidStack getFluidForItem(ItemStack item) {
		if(!containsFluid(item))
			return null;
		IFluidHandler fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
		return fluidItem.getTankProperties()[0].getContents();
	}
	
}
