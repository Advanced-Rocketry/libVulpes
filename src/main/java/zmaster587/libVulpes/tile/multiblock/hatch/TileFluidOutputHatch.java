package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.util.EmbeddedInventory;

public class TileFluidOutputHatch extends TileFluidHatch {
	
	public TileFluidOutputHatch() {
		super(LibVulpesTileEntityTypes.TILE_FLUID_OUTPUT_HATCH);
		fluidTank = new FluidTank(16000);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		return 0;
	}
	
	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.fluidohatch";
	}
	
	@Override
	public boolean isOutputOnly()
	{
		return true;
	}
}
