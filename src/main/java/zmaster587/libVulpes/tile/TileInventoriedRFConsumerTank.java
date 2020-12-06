package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.cap.FluidCapability;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class TileInventoriedRFConsumerTank extends TileInventoriedRFConsumer implements IFluidHandler {

	protected FluidTank tank;

	protected TileInventoriedRFConsumerTank(TileEntityType<?> type, int energy, int invSize, int tankSize) {
		super(type, energy,invSize);
		tank = new FluidTank(tankSize);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		CompoundNBT tanks = new CompoundNBT();
		tank.writeToNBT(tanks);

		nbt.put("tank", tanks);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if(nbt.contains("tank"))
			tank.readFromNBT((CompoundNBT)nbt.getCompound("tank"));
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		return canFill(resource.getFluid()) ? tank.fill(resource, doFill) : 0;
	}

	public boolean canFill(Fluid fluid) {
		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> new FluidCapability(this)).cast();
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public FluidStack drain(FluidStack resource,
			FluidAction doDrain) {
		return tank.drain(resource.getAmount(), doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		return tank.drain(maxDrain, doDrain);
	}
	
	@Override
	public int getTanks() {
		return tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}
}
