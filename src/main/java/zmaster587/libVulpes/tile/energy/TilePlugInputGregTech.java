package zmaster587.libVulpes.tile.energy;

import zmaster587.libVulpes.compat.GTEnergyCapability;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TilePlugInputGregTech /*extends TileForgePowerOutput*/ {

	/*public TilePlugInputGregTech() {
		super();
	}
	boolean tickedOnce = false;
	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.GTPlug";
	}

	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		if( capability == gregtech.api.capability.GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER)
			return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if( capability == gregtech.api.capability.GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER)
			return (T) new GTEnergyCapability(storage);
		return super.getCapability(capability, facing);
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
	public void update() {
		if(!world.isRemote) {
			for(Direction facing : Direction.values()) {
				TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

				if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY,  facing.getOpposite());
					this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
				}
			}
		}
	}*/
}

