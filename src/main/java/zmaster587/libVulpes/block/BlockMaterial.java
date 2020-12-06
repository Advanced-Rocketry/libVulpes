package zmaster587.libVulpes.block;

import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockMaterial extends BlockMultiblockStructure {

	public zmaster587.libVulpes.api.material.Material[] ores = new zmaster587.libVulpes.api.material.Material[16];
	

	public BlockMaterial(Properties mat) {
		super(mat);
	}
	
	/*@Override
	 public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
     {
		TileEntity tile = world.getTileEntity(pos);
		int meta = 0;
		if(tile instanceof TileMaterial) {
			meta = ((TileMaterial)tile).getMaterial();
		}

		return new ItemStack(this, 1);
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab,
			NonNullList<ItemStack> list) {
		for(int i = 0; i < ores.length && ores[i] != null; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState world, Builder builder) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();


		return list;
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// TODO Auto-generated method stub
		super.onBlockHarvested(world, pos, state, player);
		
		if(!player.isCreative()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileMaterial) {
				world.addEntity(new ItemEntity(world, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, new ItemStack(this, 1, ((TileMaterial)tile).getMaterial().getIndex())));
			}
		}
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileMaterial();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}*/
}
