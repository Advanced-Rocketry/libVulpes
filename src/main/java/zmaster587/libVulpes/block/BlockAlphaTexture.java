package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockAlphaTexture extends Block {

	public BlockAlphaTexture(Material mat) {
		super(mat);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
}
