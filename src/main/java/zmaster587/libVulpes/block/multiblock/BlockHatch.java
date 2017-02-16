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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHatch extends BlockMultiblockStructure {

	protected IIcon output;

	IIcon fluidInput;

	IIcon fluidOutput;

	private final Random random = new Random();

	public BlockHatch(Material material) {
		super(material);
		isBlockContainer = true;
	}


	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public int damageDropped(int meta) {
		return meta & 7;
	}


	@Override
	public int isProvidingWeakPower(IBlockAccess world,
			int x, int y, int z, int dir) {
		ForgeDirection direction = ForgeDirection.getOrientation(dir);
		boolean isPointer = world.getTileEntity(x - direction.offsetX , y- direction.offsetY, z - direction.offsetZ) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !(((TilePointer)world.getTileEntity(x - direction.offsetX , y- direction.offsetY, z- direction.offsetZ)).getMasterBlock() instanceof TileMultiBlock);


		return !isPointer && (world.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	public void setRedstoneState(World world, int x, int y, int z, boolean state) {
		if(world.getBlock(x, y, z) == this) {
			if(state && (world.getBlockMetadata(x, y, z) & 8) == 0) {
				world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) | 8, 3);
				world.markBlockForUpdate(x, y, z);
			}
			else if(!state && (world.getBlockMetadata(x, y, z) & 8) != 0) {
				world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) & 7, 3);
				world.markBlockForUpdate(x, y, z);
			}
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		output = iconRegister.registerIcon("libvulpes:outputHatch");
		blockIcon = iconRegister.registerIcon("libvulpes:inputHatch");
		fluidInput = iconRegister.registerIcon("libvulpes:fluidInput");
		fluidOutput = iconRegister.registerIcon("libvulpes:fluidOutput");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if((meta & 7) == 0) {
			return blockIcon;
		}else if((meta & 7) == 1 ) {
			return output;
		}
		else if((meta & 7) == 2 )
			return fluidInput;
		else
			return fluidOutput;
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
	public TileEntity createTileEntity(World world, int metadata) {
		//TODO: multiple sized Hatches
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
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile instanceof IInventory) {
			IInventory inventory = (IInventory)tile;
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if(stack == null)
					continue;

				EntityItem entityitem = new EntityItem(world, x, y, z, stack);

				float mult = 0.05F;

				entityitem.motionX = (double)((float)this.random.nextGaussian() * mult);
				entityitem.motionY = (double)((float)this.random.nextGaussian() * mult + 0.2F);
				entityitem.motionZ = (double)((float)this.random.nextGaussian() * mult);

				world.spawnEntityInWorld(entityitem);
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		boolean isPointer = access.getTileEntity(x - direction.offsetX , y- direction.offsetY, z - direction.offsetZ) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !(((TilePointer)access.getTileEntity(x - direction.offsetX , y- direction.offsetY, z- direction.offsetZ)).getMasterBlock() instanceof TileMultiBlock);
		return ( isPointer || access.getBlockMetadata(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ) < 8);
	}

	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int arg1, float arg2, float arg3,
			float arg4) {

		int meta = world.getBlockMetadata(x, y, z);
		//Handlue gui through modular system
		if((meta & 7) < 6 && !world.isRemote)
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), world, x, y, z);

		return true;
	}
}
