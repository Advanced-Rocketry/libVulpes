package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotatableBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public RotatableBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING,  Direction.byHorizontalIndex(context.getNearestLookingDirection().getHorizontalIndex()).getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

		world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	public static Direction getFront(BlockState state) {
		if(state.getBlock() instanceof RotatableBlock)
			return state.get(FACING);
		return Direction.UP;
	}
}
