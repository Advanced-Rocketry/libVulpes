package zmaster587.libVulpes.block.multiblock;

import java.util.List;
import java.util.Random;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHatch extends BlockMultiblockStructure {

	private final Random random = new Random();

	public BlockHatch(Material material) {
		super(material);
		isBlockContainer = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT,0));
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess,
			BlockPos pos, EnumFacing side) {
		side = side.getOpposite();
		boolean isPointer = blockAccess.getTileEntity(pos.offset(side)) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !(((TilePointer)blockAccess.getTileEntity(pos.offset(side))).getMasterBlock() instanceof TileMultiBlock);


		return !isPointer && (blockState.getValue(VARIANT) & 8) != 0 ? 15 : 0;
	}

	public void setRedstoneState(World world, IBlockState bstate , BlockPos pos, boolean state) {
		if(state && (bstate.getValue(VARIANT) & 8) == 0) {
			world.setBlockState(pos, bstate.withProperty(VARIANT, bstate.getValue(VARIANT) | 8));
			world.notifyBlockUpdate(pos, bstate,  bstate, 3);
		}
		else if(!state && (bstate.getValue(VARIANT) & 8) != 0) {
			world.setBlockState(pos, bstate.withProperty(VARIANT, bstate.getValue(VARIANT) & 7));
			world.notifyBlockUpdate(pos, bstate,  bstate, 3);
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		//TODO: multiple sized Hatches
		int metadata = state.getValue(VARIANT);
		if((metadata & 7) == 0)
			return new TileInputHatch(4);
		else if((metadata & 7) == 1)
			return new TileOutputHatch(4);
		else if((metadata & 7) == 2)
			return new TileFluidHatch(false);	
		else if((metadata & 7) == 3)
			return new TileFluidHatch(true);	

		return null;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof IInventory) {
			IInventory inventory = (IInventory)tile;
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if(stack == null)
					continue;

				EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);

				float mult = 0.05F;

				entityitem.motionX = (double)((float)this.random.nextGaussian() * mult);
				entityitem.motionY = (double)((float)this.random.nextGaussian() * mult + 0.2F);
				entityitem.motionZ = (double)((float)this.random.nextGaussian() * mult);

				world.spawnEntityInWorld(entityitem);
			}
		}

		super.breakBlock(world, pos, state);
	}

	
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing direction) {


		boolean isPointer = blockAccess.getTileEntity(pos.offset(direction.getOpposite())) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !(((TilePointer)blockAccess.getTileEntity(pos.offset(direction.getOpposite()))).getMasterBlock() instanceof TileMultiBlock);

		return blockState.getValue(VARIANT) < 8;

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		int meta = worldIn.getBlockState(pos).getValue(VARIANT);
		//Handlue gui through modular system
		if((meta & 7) < 6 )
			playerIn.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}
}
