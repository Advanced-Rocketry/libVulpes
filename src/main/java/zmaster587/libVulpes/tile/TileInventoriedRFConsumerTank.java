package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.cap.FluidCapability;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public abstract class TileInventoriedRFConsumerTank extends TileInventoriedRFConsumer implements IFluidHandler {

	protected FluidTank tank;

	protected TileInventoriedRFConsumerTank(int energy, int invSize, int tankSize) {
		super(energy,invSize);
		tank = new FluidTank(tankSize);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tanks = new NBTTagCompound();
		tank.writeToNBT(tanks);

		nbt.setTag("tank", tanks);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("tank"))
			tank.readFromNBT((NBTTagCompound)nbt.getTag("tank"));
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return canFill(resource.getFluid()) ? tank.fill(resource, doFill) : 0;
	}

	public boolean canFill(Fluid fluid) {
		return true;
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
	
	@Override
	public FluidStack drain(FluidStack resource,
			boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}


	@Override
	public IFluidTankProperties[] getTankProperties() {
		// TODO Auto-generated method stub
		return tank.getTankProperties();
	}
}
