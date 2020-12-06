package zmaster587.libVulpes.tile.energy;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.CreativeBattery;

public class TileCreativePowerInput extends TilePlugBase implements IPower, ITickableTileEntity {

	public TileCreativePowerInput() {
		super(LibVulpesTileEntityTypes.TILE_CREATIVE_BATTERY);
		storage = new CreativeBattery();
	}

	public TileCreativePowerInput(int teir) {
		super(LibVulpesTileEntityTypes.TILE_CREATIVE_BATTERY);
		storage = new CreativeBattery();
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		teir = nbt.getInt("teir");
		
		storage = new CreativeBattery();//new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}
	
	@Override
	public int extractEnergy(Direction dir, int amt, boolean sim) {
		return extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(Direction arg0) {
		return getUniversalEnergyStored();//getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(Direction arg0) {
		return getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(Direction arg0, int maxReceive, boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
	}


	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.creative_power_battery";
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return receiveEnergy(amt, simulate);
	}

	@Override
	public boolean canReceive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public void tick() {
		if(!world.isRemote) {
			for(Direction facing : Direction.values()) {
				TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

				if(tile != null) {
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY,  facing.getOpposite()).orElse(null);
					if(storage != null)
						this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
				}
			}
		}
	}
}
