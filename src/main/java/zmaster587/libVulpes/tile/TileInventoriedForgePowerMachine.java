package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileInventoriedForgePowerMachine extends TileInventoriedForgeProducer implements IProgressBar {

	protected int timeRemaining, currentTime, lastRFAmount;

	protected TileInventoriedForgePowerMachine(int energy, int invSize) {
		super(energy, invSize);
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
		if(worldObj.getBlockState(getPos()).getValue(BlockTile.STATE) != state)
			worldObj.setBlockState(getPos(), worldObj.getBlockState(getPos()).withProperty(BlockTile.STATE, state));

	}

	protected void onOperationFinish() {
		
		setState(false);
	};

	@Override
	public void update() {
		if(canGeneratePower()) {
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				lastRFAmount = getPowerPerOperation();
				if(!worldObj.isRemote) this.energy.acceptEnergy(lastRFAmount, false);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("timeRemaining", timeRemaining);
		nbt.setInteger("currentTime", currentTime);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		timeRemaining = nbt.getInteger("timeRemaining");
		currentTime= nbt.getInteger("currentTime");

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
