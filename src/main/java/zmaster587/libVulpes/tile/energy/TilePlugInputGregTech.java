package zmaster587.libVulpes.tile.energy;

import zmaster587.libVulpes.Configuration;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TilePlugInputGregTech extends TilePlugBase implements gregtech.api.capability.IEnergyContainer, 
ITickable {

	public TilePlugInputGregTech() {
		super();
	}
	boolean tickedOnce = false;
	@Override
	public String getModularInventoryName() {
		return "tile.GregtechPlug.name";
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public long getEnergyStored() {
		return storage.getUniversalEnergyStored();
	}

	@Override
	public long acceptEnergyFromNetwork(EnumFacing arg0, long arg1, long arg2) {
		return storage.acceptEnergy((int)(arg1*arg2*Configuration.EUMult), false);
	}

	@Override
	public long changeEnergy(long arg0) {
		if (arg0 < 0)
			return storage.extractEnergy((int) -arg0, false);
		else 
			return storage.acceptEnergy((int) arg0, false);
	}

	@Override
	public long getEnergyCapacity() {
		// TODO Auto-generated method stub
		return storage.getMaxEnergyStored();
	}

	@Override
	public long getInputAmperage() {
		return 1;
	}

	@Override
	public long getInputVoltage() {
		return 32;
	}

	@Override
	public boolean inputsEnergy(EnumFacing arg0) {
		return true;
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			for(EnumFacing facing : EnumFacing.VALUES) {
				TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

				if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY,  facing.getOpposite());
					this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
				}
			}
		}
	}
}
