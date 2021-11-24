package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.TilePointer;

public class BlockMultiBlockComponentVisible extends BlockMultiblockStructure {

	public BlockMultiBlockComponentVisible(Properties property) {
		super(property);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(VISIBLE);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TilePointer();
	}
}
