package zmaster587.libVulpes.block.multiblock;

import java.util.Random;

import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockHatch extends BlockMultiblockStructure {

	private final Random random = new Random();

	public BlockHatch(Properties properties) {
		super(properties);
	}


	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		//TODO: multiple sized Hatches
		int metadata = state.get(VARIANT);
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
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {

		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof IInventory) {
			IInventory inventory = (IInventory)tile;
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if(stack == null)
					continue;

				ItemEntity entityitem = new ItemEntity((World) world, pos.getX(), pos.getY(), pos.getZ(), stack);

				float mult = 0.05F;

				entityitem.setMotion((double)((float)this.random.nextGaussian() * mult),
				(double)((float)this.random.nextGaussian() * mult + 0.2F),
				(double)((float)this.random.nextGaussian() * mult));

				world.addEntity(entityitem);
			}
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}


	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState adjacentBlockState,
			Direction direction) {

		return (blockState.get(VARIANT) >= 8);

	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		int meta = world.getBlockState(pos).get(VARIANT);
		if((meta & 7) < 8  && !world.isRemote)
		{
			
			TileEntity te = world.getTileEntity(pos);
			if(te != null)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
		}
		return ActionResultType.SUCCESS;
	}
}
