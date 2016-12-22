package zmaster587.libVulpes.tile.energy;

import zmaster587.libVulpes.Configuration;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;

public class TilePlugInputIC2 extends TilePlugOutputRF implements IEnergySink {

	public TilePlugInputIC2() {
	}
	boolean tickedOnce = false;
	@Override
	public String getModularInventoryName() {
		return "tile.IC2Plug.name";
	}

	
	@Override
	public boolean canUpdate() {
		return true;
	}

	//TODO: find a way of doing this that doesn't tick all the time
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && !tickedOnce) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			tickedOnce = true;
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}
	
	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.min(getMaxEnergyStored() - getEnergyStored(), 128.0);
	}

	@Override
	public int getSinkTier() {
		return 2;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount,
			double voltage) {
		storage.acceptEnergy((int)(amount*Configuration.EUMult), false);
		return 0;
	}
	
	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return 0;
	}
	
}
