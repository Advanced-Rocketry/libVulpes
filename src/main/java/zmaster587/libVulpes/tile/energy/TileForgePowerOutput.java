package zmaster587.libVulpes.tile.energy;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;

public class TileForgePowerOutput extends TilePlugBase implements IEnergyStorage, ITickableTileEntity {

	public TileForgePowerOutput() {
		super(LibVulpesTileEntityTypes.TILE_FORGE_POWER_OUTPUT, 1);
	}

	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.forge_power_output";
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return acceptEnergy(amt, simulate);
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public int getEnergyStored() {
		return getUniversalEnergyStored();
	}

	@Override
	public void tick() {
		if(!world.isRemote) {
			for(Direction facing : Direction.values()) {
				TileEntity tile = world.getTileEntity(this.getPos().offset(facing));
				if(tile != null )
				{
					LazyOptional<IEnergyStorage> energyCap = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());

					if(energyCap.isPresent()) {
						IEnergyStorage storage = energyCap.orElse(null);
						this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
					}
				}
			}
		}
	}

}
