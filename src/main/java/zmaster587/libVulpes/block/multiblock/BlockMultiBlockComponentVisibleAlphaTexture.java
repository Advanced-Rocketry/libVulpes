package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockMultiBlockComponentVisibleAlphaTexture extends BlockMultiBlockComponentVisible {

	public BlockMultiBlockComponentVisibleAlphaTexture(Properties property) {
		super(property);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}
}
