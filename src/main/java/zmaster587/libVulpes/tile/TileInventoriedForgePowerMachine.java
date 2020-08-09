package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileInventoriedForgePowerMachine extends TileInventoriedForgeProducer implements IProgressBar {

	protected int timeRemaining, currentTime, lastRFAmount;

	protected TileInventoriedForgePowerMachine(TileEntityType<?> tileEntityTypeIn, int energy, int invSize) {
		super(tileEntityTypeIn, energy, invSize);
	}

	@Override
	public boolean canGeneratePower() {
		return timeRemaining > 0;
	}

	@Override
	public void onGeneratePower() {

	}

	public int getLastAmtGenerated() {
		return lastRFAmount;
	}
	
	protected void setState(boolean state) {
		BlockState bstate = world.getBlockState(getPos());
		if(bstate.getBlock() instanceof BlockTile &&  bstate.get(BlockTile.STATE) != state)
			world.setBlockState(getPos(), bstate.with(BlockTile.STATE, state));

	}

	protected void onOperationFinish() {
		
		setState(false);
	};

	@Override
	public void tick() {
		if(canGeneratePower()) {
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				lastRFAmount = getPowerPerOperation();
				if(!world.isRemote) this.energy.acceptEnergy(lastRFAmount, false);
				onGeneratePower();
				setState(true);
				if(timeRemaining < currentTime++) {
					currentTime = 0;
					timeRemaining = -1;
					onOperationFinish();
				}
			}
			else
				notEnoughBufferForFunction();
		}
		transmitPower();
	}

	@Override
	public void notEnoughBufferForFunction() {
		setState(false);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putInt("timeRemaining", timeRemaining);
		nbt.putInt("currentTime", currentTime);

		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		timeRemaining = nbt.getInt("timeRemaining");
		currentTime= nbt.getInt("currentTime");

	}

	@Override
	public float getNormallizedProgress(int id) {
		return (timeRemaining-currentTime)/(float)timeRemaining;
	}

	@Override
	public void setProgress(int id, int progress) {
		currentTime = progress;
	}

	@Override
	public int getProgress(int id) {
		return currentTime;
	}

	@Override
	public int getTotalProgress(int id) {
		return timeRemaining;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		timeRemaining = progress;
	}

}
