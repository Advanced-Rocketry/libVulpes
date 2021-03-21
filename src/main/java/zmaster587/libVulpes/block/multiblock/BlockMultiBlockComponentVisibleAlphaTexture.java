package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.TilePointer;

public class BlockMultiBlockComponentVisibleAlphaTexture extends BlockMultiblockStructure {

	public BlockMultiBlockComponentVisibleAlphaTexture(Material material) {
		super(material);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(VARIANT) > 7;
	}
	
	@Override
	public void completeStructure(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state.withProperty(VARIANT, state.getValue(VARIANT) | 8));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing direction) {
		
		return true;//blockAccess.getBlockState(pos.offset(direction)).isOpaqueCube();
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TilePointer();
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
