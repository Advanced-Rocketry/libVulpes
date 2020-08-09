package zmaster587.libVulpes.tile.energy;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;

public class TileForgePowerInput extends TilePlugBase implements IEnergyStorage {

	public TileForgePowerInput() {
		super(LibVulpesTileEntityTypes.TILE_FORGE_POWER_INPUT);
	}


	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.forge_power_input";
	}


	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return acceptEnergy(amt, simulate);
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public int getEnergyStored() {
		return getUniversalEnergyStored();
	}
	
	@Override
	public boolean canReceive() {
		return true;
	}

}
