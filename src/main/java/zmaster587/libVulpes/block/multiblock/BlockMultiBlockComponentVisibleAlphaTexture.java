package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class BlockMultiBlockComponentVisibleAlphaTexture extends BlockMultiBlockComponentVisible {

	public BlockMultiBlockComponentVisibleAlphaTexture(Material material) {
		super(material);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
