package zmaster587.libVulpes.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
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
		
		tank.readFromNBT((NBTTagCompound)nbt.getTag("tank"));
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
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
