package zmaster587.libVulpes.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

public class FluidUtils {

	public static boolean containsFluid(ItemStack stack) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null && stack != null && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).isPresent();
	}

	public static boolean containsFluid(@Nonnull ItemStack stack, @Nonnull Fluid fluid) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
			FluidStack fluidStack = fluidItem.getFluidInTank(0);
			return !fluidStack.isEmpty() && areFluidsSameType(fluidStack.getFluid(), fluid);
		}

		return false;
	}

	public static int getFluidItemCapacity(@Nonnull ItemStack stack) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
			return  fluidItem.getTankCapacity(0);
		}
		return 0;
	}

	public static IFluidHandlerItem getFluidHandler(@Nonnull ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
	}

	public static FluidStack getFluidForItem(@Nonnull ItemStack item) {
		if(!containsFluid(item))
			return new FluidStack(Fluids.EMPTY, 1000);
		IFluidHandlerItem fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
		return fluidItem.getFluidInTank(0);
	}

	//Use Forge methods to handle containers being put into inventories
	public static boolean attemptDrainContainerIInv(EmbeddedInventory inv, IFluidHandler tank, @Nonnull ItemStack stack, int inputSlot, int outputSlot) {
		if (containsFluid(stack)) {
			FluidActionResult modifiedContainer;
			if (inv.getStackInSlot(outputSlot).isEmpty()) {
				if (!(stack.getItem() == Items.BUCKET) && (getFluidForItem(stack).getAmount() == getFluidItemCapacity(stack) || tank.getTankCapacity(0) == 0)) {
					modifiedContainer = FluidUtil.tryEmptyContainer(stack, tank, getFluidItemCapacity(stack), null, true);
				} else {
					modifiedContainer = FluidUtil.tryFillContainer(stack, tank, getFluidItemCapacity(stack), null, true);
				}

				if (modifiedContainer.isSuccess()) {
					inv.getStackInSlot(inputSlot).shrink(1);
					inv.setStackInSlot(outputSlot, modifiedContainer.getResult());
				}
				return modifiedContainer.isSuccess();
			}
		}
		return false;
	}

	public static boolean areFluidsSameType(@Nonnull Fluid in, @Nonnull Fluid otherFluid) {
		return !in.isEquivalentTo(Fluids.EMPTY) && in.isEquivalentTo(otherFluid);
	}
}
