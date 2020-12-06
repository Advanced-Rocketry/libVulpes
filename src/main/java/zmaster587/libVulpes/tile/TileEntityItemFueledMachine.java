package zmaster587.libVulpes.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityItemFueledMachine extends TileEntityMachine {
	
	public TileEntityItemFueledMachine(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	protected int fuelTime, maxFuelTime;
	
	@Override
	public void tick() {
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
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		
		nbt.putInt("fuelTime", fuelTime);
		nbt.putInt("maxFuelTime", maxFuelTime);
		return nbt;
	}
	
	
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		fuelTime = nbt.getInt("fuelTime");
		maxFuelTime = nbt.getInt("maxFuelTime");
	}
}
