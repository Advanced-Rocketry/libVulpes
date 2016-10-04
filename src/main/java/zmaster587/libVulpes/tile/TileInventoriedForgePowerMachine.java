package zmaster587.libVulpes.tile;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileInventoriedForgePowerMachine extends TileInventoriedForgeProducer {

	protected int timeRemaining, currentTime;
	
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
	
	protected void onOperationFinish() {};
	
	@Override
	public void update() {
		if(canGeneratePower()) {
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!worldObj.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
				
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
	
}
