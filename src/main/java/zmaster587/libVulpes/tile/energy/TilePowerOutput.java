package zmaster587.libVulpes.tile.energy;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import zmaster587.libVulpes.energy.IPower;

public class TilePowerOutput extends TilePlugBase implements IPower, ITickableTileEntity {

	public TilePowerOutput(TileEntityType<?> type) {
		super(type, 1);
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
	public void tick() {

		if(!world.isRemote) {
			for(Direction dir : Direction.values()) {

				TileEntity tile = world.getTileEntity(this.pos.offset(dir));

				if(tile instanceof IPower) {
					IPower handle = (IPower)tile;
					storage.extractEnergy(handle.receiveEnergy(dir.getOpposite(), storage.getUniversalEnergyStored(), false), false);
				}
			}
		}
	}

	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.power_output";
	}

	@Override
	public int extractEnergy(Direction dir, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(Direction dir) {
		return storage.getUniversalEnergyStored();
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

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
}
