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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote)
		{
			
			TileEntity te = world.getTileEntity(pos);
			if(te != null)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, buf -> {buf.writeInt(((IModularInventory)te).getModularInvType().ordinal()); buf.writeBlockPos(pos); });
		}
		return ActionResultType.SUCCESS;
	}
}
