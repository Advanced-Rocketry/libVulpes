package zmaster587.libVulpes.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import zmaster587.libVulpes.event.BucketHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FluidUtils {

	private static Map<ResourceLocation, List<ResourceLocation>> fluidEquivilentMapping = new HashMap<ResourceLocation, List<ResourceLocation>>();

	public static boolean containsFluid(ItemStack stack) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null && stack != null && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).isPresent();
	}

	public static boolean containsFluid(@Nonnull ItemStack stack, Fluid fluid) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
			FluidStack fluidStack = fluidItem.getFluidInTank(0);
			if(fluidStack != null && areFluidsSameType(fluidStack.getFluid(), fluid))
				return true;
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

	public static IFluidHandlerItem getFluidHandler(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
	}

	public static FluidStack getFluidForItem(@Nonnull ItemStack item) {
		if(!containsFluid(item))
			return null;
		IFluidHandlerItem fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
		return fluidItem.getFluidInTank(0);
	}



	//Use Forge methods to handle containers being put into inventories
	public static boolean attemptDrainContainerIInv(EmbeddedInventory inv, IFluidHandler tank, @Nonnull ItemStack stack, int inputSlot, int outputSlot) {
		if (containsFluid(stack)) {
			boolean fill = false;
			boolean toReturn = false;
			FluidActionResult modifiedContainer = null;
			if (!(stack.getItem() == Items.BUCKET) && (getFluidForItem(stack).getAmount() == getFluidItemCapacity(stack) || tank.getTankCapacity(0) == 0)) {
				modifiedContainer = FluidUtil.tryEmptyContainer(stack, tank, getFluidItemCapacity(stack), null, false);
			} else {
				modifiedContainer = FluidUtil.tryFillContainer(stack, tank, getFluidItemCapacity(stack), null, false);
				fill = true;
			}
				if (modifiedContainer.isSuccess()) {
					if (inv.getStackInSlot(outputSlot).isEmpty()) {
						toReturn = fill ? FluidUtil.tryFillContainer(stack, tank, getFluidItemCapacity(stack), null, true).isSuccess() : FluidUtil.tryEmptyContainer(stack, tank, getFluidItemCapacity(stack), null, true).isSuccess();
						inv.getStackInSlot(inputSlot).shrink(1);
						inv.setStackInSlot(outputSlot, modifiedContainer.getResult());
					} else if ((inv.getStackInSlot(outputSlot).getItem() == modifiedContainer.getResult().getItem() && inv.getStackInSlot(outputSlot).getCount() < inv.getStackInSlot(outputSlot).getMaxStackSize())) {
						toReturn = fill ? FluidUtil.tryFillContainer(stack, tank, getFluidItemCapacity(stack), null, true).isSuccess() : FluidUtil.tryEmptyContainer(stack, tank, getFluidItemCapacity(stack), null, true).isSuccess();
						inv.getStackInSlot(inputSlot).shrink(1);
						inv.getStackInSlot(outputSlot).grow(1);
						return true;
					}
				}
                return toReturn;
			}
		return false;
	}

	public static void addFluidMapping(Fluid in, ResourceLocation altName)
	{
		ResourceLocation fluidKeyName = in.getRegistryName();
		addFluidMapping(fluidKeyName, altName);
		addFluidMapping(altName, fluidKeyName);
	}

	private static void addFluidMapping(ResourceLocation in, ResourceLocation altName)
	{
		ResourceLocation fluidKeyName = in;
		List<ResourceLocation> mappedValues;
		if(!fluidEquivilentMapping.containsKey(fluidKeyName))
		{
			mappedValues = new LinkedList<ResourceLocation>();
			fluidEquivilentMapping.put(fluidKeyName, mappedValues);
		}
		else
			mappedValues = fluidEquivilentMapping.get(fluidKeyName);

		mappedValues.add(altName);
	}

	public static boolean areFluidsSameType(ResourceLocation inFluidName, Fluid otherFluid) {
		if(otherFluid == null)
			return false;
		ResourceLocation otherFluidName = otherFluid.getRegistryName();

		return areFluidsSameType(inFluidName, otherFluidName);
	}

	public static boolean areFluidsSameType(ResourceLocation inFluidName, ResourceLocation otherFluidName) {
		if(inFluidName == null)
			inFluidName = Fluids.EMPTY.getRegistryName();
		if(otherFluidName == null)
			otherFluidName = Fluids.EMPTY.getRegistryName();

		if(inFluidName.equals(otherFluidName))
			return true;

		return fluidEquivilentMapping.containsKey(inFluidName) && fluidEquivilentMapping.get(inFluidName).contains(otherFluidName);
	}

	public static boolean areFluidsSameType(Fluid in, Fluid otherFluid) {
		if(in == null || otherFluid == null)
			return false;
		ResourceLocation inFluidName = in.getRegistryName();
		ResourceLocation otherFluidName = otherFluid.getRegistryName();
		return areFluidsSameType(inFluidName, otherFluidName);
	}
}
