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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockFullyRotatable extends Block {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public BlockFullyRotatable(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}


	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

		if(Math.abs(placer.rotationPitch) > 60) {
			world.setBlockState(pos, state.with(FACING, placer.rotationPitch > 0 ? Direction.UP : Direction.DOWN), 2);
		}
		else
			world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	public static Direction getFront(BlockState state) {
		return state.get(FACING);
	}
}
