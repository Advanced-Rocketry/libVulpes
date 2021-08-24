package zmaster587.libVulpes.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.IComparatorOverride;

public class BlockTileComparatorOverride extends BlockTile {

	public BlockTileComparatorOverride(Properties properties, zmaster587.libVulpes.inventory.GuiHandler.guiId guiId) {
		super(properties, guiId);
		this.guiId = guiId;
		this.setDefaultState(this.stateContainer.getBaseState().with(STATE, false));
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IComparatorOverride) {
        	return ((IComparatorOverride) tile).getComparatorOverride();
		}
        return 0;
	}
}