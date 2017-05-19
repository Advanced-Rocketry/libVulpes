package zmaster587.libVulpes.block.multiblock;

import javax.annotation.Nullable;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * hosts a multiblock machine master tile
 *
 */
public class BlockMultiblockMachine extends BlockTile {

	
	
	public BlockMultiblockMachine(Class<? extends TileMultiBlock> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		

		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete())
				tileMulti.deconstructMultiBlock(worldIn, pos, false, state);
		}
		super.breakBlock(worldIn, pos,	state);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess access, BlockPos pos2, EnumFacing direction) {
		
		BlockPos pos = pos2;//pos2.offset(direction.getOpposite());
		
		TileEntity tile = access.getTileEntity(pos2);
		if(tile instanceof TileMultiBlock) {
			return !((TileMultiBlock)tile).shouldHideBlock(tile.getWorld(), pos, access.getBlockState(pos)) || !((TileMultiBlock)tile).canRender();
		}
		
		return super.shouldSideBeRendered(blockState , access, pos, direction);
	}
	
	/*@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		
		TileEntity tile = access.getTileEntity(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ);
		if(tile instanceof TileMultiBlock) {
			return !((TileMultiBlock)tile).shouldHideBlock(tile.getWorldObj(), x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ, access.getBlock(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ)) || !((TileMultiBlock)tile).canRender();
		}
		return super.shouldSideBeRendered(access, x, y, z, side);
	}*/
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ)  {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete() && !worldIn.isRemote) {
				if(tile instanceof IModularInventory)
					playerIn.openGui(LibVulpes.instance, guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			else
				return tileMulti.attemptCompleteStructure(state);
		}
		return true;
	}
}
