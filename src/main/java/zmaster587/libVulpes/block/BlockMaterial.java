package zmaster587.libVulpes.block;

import java.util.ArrayList;
import java.util.List;

import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.tile.TileMaterial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMaterial extends BlockMultiblockStructure {

	public zmaster587.libVulpes.api.material.Material[] ores = new zmaster587.libVulpes.api.material.Material[16];
	

	public BlockMaterial(net.minecraft.block.material.Material mat) {
		super(mat);
		this.isBlockContainer = true;
	}

	@Override
	 public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
     {
		TileEntity tile = world.getTileEntity(pos);
		int meta = 0;
		if(tile instanceof TileMaterial) {
			meta = ((TileMaterial)tile).getMaterial().getMeta();
		}

		return new ItemStack(this, 1, meta);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < ores.length && ores[i] != null; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
			IBlockState state, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();


		return list;
	}


	@Override
	public void onBlockHarvested(World world, BlockPos pos,
			IBlockState state, EntityPlayer player) {
		// TODO Auto-generated method stub
		super.onBlockHarvested(world, pos, state, player);
		
		if(!player.capabilities.isCreativeMode) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileMaterial) {
				world.spawnEntityInWorld(new EntityItem(world, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, new ItemStack(this, 1, ((TileMaterial)tile).getMaterial().getIndex())));
			}
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileMaterial();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
