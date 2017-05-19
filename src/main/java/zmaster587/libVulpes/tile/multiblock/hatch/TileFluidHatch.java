package zmaster587.libVulpes.tile.multiblock.hatch;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import zmaster587.libVulpes.cap.FluidCapability;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.IconResource;

public class TileFluidHatch extends TilePointer implements IFluidHandlerInternal, IModularInventory, ISidedInventory {

	protected FluidTank fluidTank;
	private EmbeddedInventory inventory;
	private boolean outputOnly;
	
	public TileFluidHatch() {
		fluidTank = new FluidTank(16000);
		inventory = new EmbeddedInventory(2);
	}
	
	public TileFluidHatch(int capacity) {
		fluidTank = new FluidTank(capacity);
		inventory = new EmbeddedInventory(2);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) new FluidCapability(this);
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		
	}

	public TileFluidHatch(boolean outputOnly) {
		this();

		this.outputOnly = outputOnly;
	}

	public boolean isOutputOnly() {
		return outputOnly;
	}
	
	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if(outputOnly)
			return 0;
		return fillInternal(resource, doFill);

	}

	
	@Override
	public FluidStack drain(FluidStack resource,
			boolean doDrain) {

		if(resource.isFluidEqual(fluidTank.getFluid())) {
			FluidStack fluidStack = fluidTank.drain(resource.amount, doDrain);
			while(useBucket(0, getStackInSlot(0)));
			
			
			return fluidStack;
		}
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return fluidTank.drain(maxDrain, doDrain);
	}
	@Override
	public FluidStack drainInternal(FluidStack maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public int fillInternal(FluidStack resource, boolean doFill) {
		if(doFill && this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
		
		
		int fillAmt = fluidTank.fill(resource, doFill);
		while(useBucket(0, getStackInSlot(0)));
		
		
		return fillAmt;
	}

	@Override
	public FluidStack drainInternal(int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}
	
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return fluidTank.getTankProperties();
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = new ArrayList<ModuleBase>();

		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		if(world.isRemote)
			list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
		list.add(new ModuleLiquidIndicator(27, 18, this));

		return list;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.liquidHatch.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean("outputOnly", outputOnly);
		inventory.writeToNBT(nbt);
		nbt.setInteger("capacity", fluidTank.getCapacity());
		fluidTank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		outputOnly = nbt.getBoolean("outputOnly");
		inventory.readFromNBT(nbt);
		fluidTank = new FluidTank(nbt.getInteger("capacity"));
		fluidTank.readFromNBT(nbt);
	}

	//Yes i was lazy
	//TODO: make better
	protected boolean useBucket( int slot, ItemStack stack) {

		//Fill tank from bucket
		//Drain tank to bucket
		if(stack != null && stack.getItem() instanceof IFluidHandlerItem) {
			IFluidHandlerItem fluidItem = ((IFluidHandlerItem)stack.getItem());
			FluidStack fluidStack;
			stack = stack.copy();
			stack.setCount(1);
			
			IFluidTankProperties tankProps = fluidItem.getTankProperties()[0];
			
			//Drain the tank into the item
			if(fluidTank.getFluid() != null && (tankProps.getContents() == null || outputOnly)) {
				int amt = fluidItem.fill(fluidTank.getFluid(), true);
				
				
				//If the container is full move it down and try again for a new one
				if(amt != 0 && tankProps.getCapacity() == tankProps.getContents().amount) {
					
					
					if(getStackInSlot(1) == null) {
						inventory.setInventorySlotContents(1, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stack) && getStackInSlot(1).getItem().equals(stack.getItem()) && getStackInSlot(1).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) < getStackInSlot(1).getCount()) {
						getStackInSlot(1).setCount(getStackInSlot(1).getCount() + 1);

					}
					else
						return false;
					fluidTank.drain(amt, true);
					decrStackSize(0, 1);

					return true;
				}
				
			}
			else {
				fluidStack = fluidItem.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(), false);

				int amountDrained = fluidTank.fill(fluidStack, false);
				
				//prevent bucket eating
				if(amountDrained == 0)
					return false;
				
				fluidItem.drain(amountDrained, true);
				if (tankProps.getContents() == null || tankProps.getContents().amount == 0) {
					if(getStackInSlot(1) == null) {
						inventory.setInventorySlotContents(1, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stack) && getStackInSlot(1).getItem().equals(stack.getItem()) && getStackInSlot(1).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) > getStackInSlot(1).getCount()) {
						getStackInSlot(1).setCount(getStackInSlot(1).getCount() + 1);

					}
					else
						return false;

					decrStackSize(0, 1);
					fluidTank.fill(fluidStack, true);
					
					return true;
					
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {0,1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return index == 0 && isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return index == 1;
	}
}
