package zmaster587.libVulpes.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockRotatableModel extends RotatableBlock {
	
	public BlockRotatableModel(Material par2Material) {
		super(par2Material);
		
	}
	
	 @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	
}
