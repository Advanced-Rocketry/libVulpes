/*package zmaster587.libVulpes.tile.energy;

import zmaster587.libVulpes.energy.IPower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class TilePlugOutputRF extends TilePlugBase implements IPower, ITickable {

	public TilePlugOutputRF() {
		super(1);
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
	}


	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		for(Direction dir : Direction.values()) {
			
			TileEntity tile = worldObj.getTileEntity(this.pos.offset(dir));

			if(tile instanceof IEnergyReceiver) {
				IEnergyReceiver handle = (IEnergyReceiver)tile;
				storage.getEnergyStored();
				storage.extractEnergy(handle.receiveEnergy(dir.getOpposite(), storage.getEnergyStored(), false), false);
			}
		}
	}

	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.rfOutput";
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public int extractEnergy(Direction dir, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(Direction dir) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(Direction arg0) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(Direction dir, int amt, boolean simulate) {

		return 0;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return storage.acceptEnergy(amt, simulate);
	}

}*/
