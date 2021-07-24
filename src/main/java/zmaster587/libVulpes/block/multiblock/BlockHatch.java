package zmaster587.libVulpes.block.multiblock;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class BlockHatch extends BlockMultiblockStructure {

	protected TileEntityType<?> tileClass;
	private final Random random = new Random();
	public BlockHatch(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	//MUST be called after construction.  The Tile type doesn't yet exist when the block is contructed
	public BlockHatch _setTile(TileEntityType<?> tileClass)
	{
		this.tileClass = tileClass;
		return this;
	}


	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return tileClass.create();
	}


	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {

		if(state.getBlock() != newState.getBlock())
		{
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof IInventory) {
				IInventory inventory = (IInventory)tile;
				for(int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack stack = inventory.getStackInSlot(i);

					if(stack.isEmpty())
						continue;

					ItemEntity entityitem = new ItemEntity((World) world, pos.getX(), pos.getY(), pos.getZ(), stack);

					float mult = 0.05F;

					entityitem.setMotion((double)((float)this.random.nextGaussian() * mult),
							(double)((float)this.random.nextGaussian() * mult + 0.2F),
							(double)((float)this.random.nextGaussian() * mult));

					world.addEntity(entityitem);
				}
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}


	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote) {

			TileEntity te = world.getTileEntity(pos);
			if (te != null)
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, buf -> {
					buf.writeInt(((IModularInventory) te).getModularInvType().ordinal());
					buf.writeBlockPos(pos);
				});
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		//Fluid comparator size
		if (tile instanceof TileFluidHatch)
			return (15 * ((TileFluidHatch) tile).getFluidTank().getFluidAmount())/((TileFluidHatch) tile).getFluidTank().getCapacity();
		//Otherwise, try if it's an inventory - this code is adapted from how chests do it because we don't extend BlockContainer
		else if (tile instanceof TileInventoryHatch) {
			TileInventoryHatch tile2 = ((TileInventoryHatch) tile);
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < tile2.getSizeInventory(); ++j) {
				ItemStack itemstack = tile2.getStackInSlot(j);

				if (!itemstack.isEmpty()) {
					f += (float) itemstack.getCount() / (float) Math.min(tile2.getInventoryStackLimit(), itemstack.getMaxStackSize());
					i++;
				}
			}
			f = f / (float) tile2.getSizeInventory();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
		//Failing that, we do nothing
		return 0;
	}
}
