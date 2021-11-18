package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHidableBlock {
	
	void hideBlock(World world, BlockPos pos, BlockState state);

	void showBlock(World world, BlockPos pos, BlockState state);
}
