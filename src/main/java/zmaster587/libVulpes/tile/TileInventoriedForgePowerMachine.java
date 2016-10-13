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

	protected void setState(boolean on) {
		if(on)
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) | 8, 2);
		else
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & (~8), 2);

	}

	protected void onOperationFinish() {
		setState(false);
	};

	public int getLastAmtGenerated() {
		return lastRFAmount;
	}
	
	@Override
	public void updateEntity() {
		lastRFAmount = 0;
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
	}

	@Override
	public void notEnoughBufferForFunction() {
		setState(false);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("timeRemaining", timeRemaining);
		nbt.setInteger("currentTime", currentTime);
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
