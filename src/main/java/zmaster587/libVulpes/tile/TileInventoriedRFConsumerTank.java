package zmaster587.libVulpes.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileInventoriedRFConsumerTank extends TileInventoriedRFConsumer implements IFluidHandler {

	protected FluidTank tank;
	
	protected TileInventoriedRFConsumerTank(int energy, int invSize, int tankSize) {
		super(energy,invSize);
		tank = new FluidTank(tankSize);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tanks = new NBTTagCompound();
		tank.writeToNBT(tanks);
		
		nbt.setTag("tank", tanks);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		tank.readFromNBT((NBTTagCompound)nbt.getTag("tank"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tank.getCapacity() > 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		// TODO Auto-generated method stub
		return new FluidTankInfo[] {tank.getInfo()};
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tank.getCapacity() > 0;
	}
}
