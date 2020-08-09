package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class BlockTile extends RotatableBlock {

	protected TileEntityType<?> tileClass;
	protected int guiId;
	public static final BooleanProperty STATE = BooleanProperty.create("state");

	public BlockTile(Properties properties, int guiId) {
		super(properties);
		
		this.guiId = guiId;
		this.setDefaultState(this.stateContainer.getBaseState().with(STATE, false));
	}
	
	//MUST be called after construction.  The Tile type doesn't yet exist when the block is contructed
	public void _setTile(TileEntityType<?> tileClass)
	{
		this.tileClass = tileClass;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		builder.add(FACING, STATE);
	}
	
	public void setBlockState(World world, BlockState state, BlockPos pos, boolean newState) {
		world.setBlockState(pos, state.with(STATE, newState), 2);
		world.markBlockRangeForRenderUpdate(pos, state, state.with(STATE, newState));
	}
    
	@Override
	public boolean hasTileEntity(BlockState state) { return true; }
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return tileClass.create();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if(te != null)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos,
				neighbor);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();

	}
	
	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state)
	{
		TileEntity tile = world.getTileEntity(pos);

		//This code could use some optimization -Dark
		if (!world.isRemote() && tile instanceof IInventory)
		{
			IInventory inventory = (IInventory)tile;
			for (int i1 = 0; i1 < inventory.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = inventory.getStackInSlot(i1);

				if (!itemstack.isEmpty())
				{
					float f = world.getRandom().nextFloat() * 0.8F + 0.1F;
					float f1 = world.getRandom().nextFloat() * 0.8F + 0.1F;
					ItemEntity entityitem;

					for (float f2 = world.getRandom().nextFloat() * 0.8F + 0.1F; itemstack.getCount() > 0; world.addEntity(entityitem))
					{
						int j1 = world.getRandom().nextInt(21) + 10;

						if (j1 > itemstack.getCount())
						{
							j1 = itemstack.getCount();
						}
						Item oldItem = itemstack.getItem();
						ItemStack oldStack = itemstack.copy();
						itemstack.setCount(itemstack.getCount() - j1);
						entityitem = new ItemEntity((World) world, (double)((float)pos.getX() + f), (double)((float)pos.getY() + f1), (double)((float)pos.getZ() + f2), new ItemStack(oldItem, j1, itemstack.getTag()));
						float f3 = 0.05F;
						entityitem.setMotion((double)((float)world.getRandom().nextGaussian() * f3),
						(double)((float)world.getRandom().nextGaussian() * f3 + 0.2F),
						(double)((float)world.getRandom().nextGaussian() * f3));

						if (oldStack.hasTag())
						{
							entityitem.getItem().setTag((CompoundNBT)oldStack.getTag().copy());
						}
						world.addEntity(entityitem);
					}
				}
			}

		}

		super.onPlayerDestroy(world, pos, state);
	}
}
