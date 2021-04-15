package zmaster587.libVulpes.util;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import zmaster587.libVulpes.event.BucketHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FluidUtils {
	
	private static Map<String, List<String>> fluidEquivilentMapping = new HashMap<String, List<String>>();

	public static boolean containsFluid(ItemStack stack) {
		return !stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
	}

	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
			FluidStack fluidStack = fluidItem.getTankProperties()[0].getContents();
			if(fluidStack != null && areFluidsSameType(fluidStack.getFluid(), fluid))
				return true;
		}

		return false;
	}

	public static int getFluidItemCapacity(ItemStack stack) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
			return  fluidItem.getTankProperties()[0].getCapacity();
		}
		return 0;
	}

	public static IFluidHandlerItem getFluidHandler(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
	}

	public static FluidStack getFluidForItem(ItemStack item) {
		if(!containsFluid(item))
			return null;
		IFluidHandlerItem fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
		return fluidItem.getTankProperties()[0].getContents();
	}

	//Use Forge methods to handle containers being put into inventories
	public static boolean attemptDrainContainerIInv(EmbeddedInventory inv, IFluidHandler tank, ItemStack stack, int inputSlot, int outputSlot) {
		if (containsFluid(stack)) {
			boolean fill = false;
			boolean toReturn = false;
			FluidActionResult modifiedContainer = null;
			if (!(stack.getItem() == Items.BUCKET) && (getFluidForItem(stack).amount == getFluidItemCapacity(stack) || tank.getTankProperties()[0].getContents().amount == 0)) {
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
	
	public static void addFluidMapping(Fluid in, String altName)
	{
		String fluidKeyName = in.getName();
		addFluidMapping(fluidKeyName, altName);
		addFluidMapping(altName, fluidKeyName);
	}
	
	private static void addFluidMapping(String in, String altName)
	{
		String fluidKeyName = in;
		List<String> mappedValues;
		if(!fluidEquivilentMapping.containsKey(fluidKeyName))
		{
			mappedValues = new LinkedList<String>();
			fluidEquivilentMapping.put(fluidKeyName, mappedValues);
		}
		else
			mappedValues = fluidEquivilentMapping.get(fluidKeyName);
		
		mappedValues.add(altName);
	}
	
	public static boolean areFluidsSameType(Fluid in, Fluid otherFluid) {
		if(in == null || otherFluid == null)
			return false;
		String inFluidName = in.getName();
		String otherFluidName = otherFluid.getName();
		
		if(inFluidName.equals(otherFluidName))
			return true;
		
		return fluidEquivilentMapping.containsKey(inFluidName) && fluidEquivilentMapping.get(inFluidName).contains(otherFluidName);
	}
}
