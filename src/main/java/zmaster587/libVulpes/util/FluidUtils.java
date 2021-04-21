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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import zmaster587.libVulpes.event.BucketHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FluidUtils {
	
	private static Map<ResourceLocation, List<ResourceLocation>> fluidEquivilentMapping = new HashMap<ResourceLocation, List<ResourceLocation>>();

	public static boolean containsFluid(ItemStack stack) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null && stack != null && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).isPresent();
	}

	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
			FluidStack fluidStack = fluidItem.getFluidInTank(0);
			if(fluidStack != null && areFluidsSameType(fluidStack.getFluid(), fluid))
				return true;
		}

		return false;
	}

	public static int getFluidItemCapacity(ItemStack stack) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
			return  fluidItem.getTankCapacity(0);
		}
		return 0;
	}

	public static IFluidHandlerItem getFluidHandler(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
	}

	public static FluidStack getFluidForItem(ItemStack item) {
		if(!containsFluid(item))
			return null;
		IFluidHandlerItem fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);
		return fluidItem.getFluidInTank(0);
	}

	public static boolean attemptDrainContainerIInv(EmbeddedInventory inv, IFluidTank tank, ItemStack stack, int inputSlot, int outputSlot) {

		if(containsFluid(stack)) {
			
			FluidStack fluidStack;
			stack = stack.copy();
			stack.setCount(1);
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElse(null);

			FluidStack itemFluidStack = getFluidForItem(stack);

			//Drain the tank into the item
			if(itemFluidStack == null && tank.getFluid() != null) {
				int amt = 0;
				
				//Special case handling buckets
				if(stack.getItem() == Items.BUCKET)
				{
					if(tank.getFluidAmount() > 1000)
					{
						Item newBucket = BucketHandler.INSTANCE.getItemFromFluid(tank.getFluid().getFluid());
						if(newBucket != null)
						{
							stack = new ItemStack(newBucket);
							amt = 1000;
							
						}
					}
				}
				//General case
				else
				{
					amt = fluidItem.fill(tank.getFluid(), FluidAction.EXECUTE);
					stack = fluidItem.getContainer();
				}

				//If the container is full move it down and try again for a new one
				if(amt != 0 && getFluidItemCapacity(stack) == getFluidForItem(stack).getAmount()) {


					if(inv.getStackInSlot(outputSlot).isEmpty()) {
						inv.setInventorySlotContents(outputSlot, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(inv.getStackInSlot(outputSlot), stack) && inv.getStackInSlot(outputSlot).getItem().equals(stack.getItem()) && inv.getStackInSlot(outputSlot).getDamage() == stack.getDamage() && stack.getItem().getItemStackLimit(stack) < inv.getStackInSlot(outputSlot).getCount()) {
						inv.getStackInSlot(outputSlot).setCount(inv.getStackInSlot(outputSlot).getCount() + 1);

					}
					else
						return false;
					tank.drain(amt, FluidAction.EXECUTE);
					inv.decrStackSize(inputSlot, 1);

					return true;
				}

			}
			else {
				fluidStack = fluidItem.drain(tank.getCapacity() - tank.getFluidAmount(), FluidAction.SIMULATE);

				int amountDrained = tank.fill(fluidStack, FluidAction.SIMULATE);
				FluidStack fluidStack2 = fluidItem.drain(amountDrained, FluidAction.EXECUTE);
				
				stack = fluidItem.getContainer();
				
				if (getFluidForItem(stack) == null || (fluidStack2 != null && fluidStack2.getAmount() != 0)) {
					if(inv.getStackInSlot(outputSlot).isEmpty()) {
						inv.setInventorySlotContents(outputSlot, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(inv.getStackInSlot(outputSlot), stack) && inv.getStackInSlot(outputSlot).getItem().equals(stack.getItem()) && inv.getStackInSlot(outputSlot).getDamage() == stack.getDamage() && stack.getItem().getItemStackLimit(stack) > inv.getStackInSlot(outputSlot).getCount()) {
						inv.getStackInSlot(outputSlot).setCount( inv.getStackInSlot(outputSlot).getCount() + 1 );

					}
					else
						return false;
					tank.fill(fluidStack, FluidAction.EXECUTE);
					inv.decrStackSize(inputSlot, 1);

					return true;
				}
			}
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
