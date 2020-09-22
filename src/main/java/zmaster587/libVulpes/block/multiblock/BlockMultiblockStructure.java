package zmaster587.libVulpes.block.multiblock;

import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Block which is integrated into the multiblock structure.  When a structure is formed the block
 * continues to render and when broken will alert the master block
 * most significant damage bit indicates if the block is fully formed
 */
public class BlockMultiblockStructure extends Block implements IHidableBlock {
	
	VoxelShape almostFull = VoxelShapes.create(0.0001, 0.0001, 0.0001, 0.9999, 0.9999, 0.9999);
	
	//public static final IntegerProperty VARIANT = IntegerProperty.create("varient", 0, 15);
	public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
	protected BlockMultiblockStructure(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(VISIBLE,true));
	}

	@Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	      builder.add(VISIBLE);
	   }
    
	/**
	 * Turns the block invisible or in the case of BlockMultiBlockComponentVisible makes it create a tileEntity
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 */
	@Override
	public void hideBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VISIBLE, false));
	}
	
	@Override
	public void showBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VISIBLE, true));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return state.get(VISIBLE) ? super.getShape(state, worldIn, pos, context) : almostFull;
	}
	
	
	
	// loot tables
	
	public void completeStructure(World world, BlockPos pos, BlockState state) {
		
	}

	public void destroyStructure(World world, BlockPos pos, BlockState state) {
	}
	
	@Override
	public boolean isSideInvisible(net.minecraft.block.BlockState state,
			net.minecraft.block.BlockState adjacentBlockState, Direction side) {
		
		return super.isSideInvisible(state, adjacentBlockState, side) || !state.get(VISIBLE);
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
