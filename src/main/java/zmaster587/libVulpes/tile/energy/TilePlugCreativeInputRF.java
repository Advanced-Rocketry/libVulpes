package zmaster587.libVulpes.tile.energy;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import zmaster587.libVulpes.util.CreativeBattery;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePlugCreativeInputRF extends TilePlugBase implements IEnergyHandler {

	
	public TilePlugCreativeInputRF() {
		super(1);
		storage = new CreativeBattery();
	}

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean sim) {
		return extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection arg0, int maxReceive, boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}


	@Override
	public String getModularInventoryName() {
		return "tile.creativePowerBattery.name";
	}
	
	@Override
	public String getInventoryName() {
		return getModularInventoryName();
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

				if(tile instanceof IEnergyReceiver) {
					IEnergyReceiver handle = (IEnergyReceiver)tile;
					storage.extractEnergy(handle.receiveEnergy(dir.getOpposite(), storage.getEnergyStored(), false), false);
				}
			}
		}
	}

}
