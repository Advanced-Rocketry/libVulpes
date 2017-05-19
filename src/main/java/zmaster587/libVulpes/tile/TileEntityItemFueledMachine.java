package zmaster587.libVulpes.tile;

import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityItemFueledMachine extends TileEntityMachine {
	protected int fuelTime, maxFuelTime;
	
	@Override
	public void update() {
		if(fuelTime > 0) {
			fuelTime--;
			if(fuelTime == 0) {
				maxFuelTime = 0;
				
				//Run update to check for more fuel
				onInventoryUpdate();
				if(fuelTime == 0)
					setRunning(false, world);
					
			}
		}
	}
	
	public int getFuelTime() {return fuelTime;}
	
	public int getMaxFuelTime() { return maxFuelTime; }
	
	public void setFuelTime(int time) { fuelTime = time; }
	
	public void setMaxFuelTime(int time) { maxFuelTime = time; setFuelTime(time); }
	
	public boolean isBurningFuel() { return fuelTime > 0; }
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setInteger("fuelTime", fuelTime);
		nbt.setInteger("maxFuelTime", maxFuelTime);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		fuelTime = nbt.getInteger("fuelTime");
		maxFuelTime = nbt.getInteger("maxFuelTime");
	}
}
