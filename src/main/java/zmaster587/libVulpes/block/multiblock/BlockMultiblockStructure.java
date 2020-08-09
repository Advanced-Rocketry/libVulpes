package zmaster587.libVulpes.block.multiblock;

import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block which is integrated into the multiblock structure.  When a structure is formed the block
 * continues to render and when broken will alert the master block
 * most significant damage bit indicates if the block is fully formed
 */
public class BlockMultiblockStructure extends Block {
	
	public static final IntegerProperty VARIANT = IntegerProperty.create("varient", 0, 15);
	protected BlockMultiblockStructure(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(VARIANT,0));
	}

	@Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	      builder.add(VARIANT);
	   }
    
	/**
	 * Turns the block invisible or in the case of BlockMultiBlockComponentVisible makes it create a tileEntity
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 */
	public void hideBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VARIANT, state.get(VARIANT) | 8));
	}
	
	// loot tables
	/*@Override
	public int damageDropped(BlockState state) {
		return state.get(VARIANT) & 7;
	}*/
	
	public void completeStructure(World world, BlockPos pos, BlockState state) {
		
	}

	public void destroyStructure(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VARIANT, state.get(VARIANT) & 7));
	}
	
	@Override
	public boolean isSideInvisible(net.minecraft.block.BlockState state,
			net.minecraft.block.BlockState adjacentBlockState, Direction side) {
		
		return super.isSideInvisible(state, adjacentBlockState, side) && (state.get(VARIANT) & 8) != 0;
	}
	
	@Override
	public void onReplaced(net.minecraft.block.BlockState state, World world, BlockPos pos,
			net.minecraft.block.BlockState newState, boolean isMoving) {
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IMultiblock) {
			IMultiblock tileMulti = (IMultiblock)tile;
			
			if(tileMulti.hasMaster()) {
				if(tileMulti.getMasterBlock() instanceof TileMultiBlock)
					((TileMultiBlock)tileMulti.getMasterBlock()).deconstructMultiBlock(world, pos,true, world.getBlockState(tileMulti.getMasterBlock().getPos()));
			}
		}
		
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
