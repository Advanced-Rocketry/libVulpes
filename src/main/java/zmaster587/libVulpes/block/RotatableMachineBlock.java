package zmaster587.libVulpes.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

import net.minecraft.block.AbstractBlock.Properties;

public class RotatableMachineBlock extends RotatableBlock {
	protected final Random random = new Random();
	
	static  BooleanProperty STATE = BooleanProperty.create("state");

	public RotatableMachineBlock(Properties par2Material) {
		super(par2Material);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(STATE);
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		Style style = Style.EMPTY.setFormatting(TextFormatting.ITALIC);
		
		tooltip.add( new TranslationTextComponent("machine.tooltip.multiblock").mergeStyle(style));
	}

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) 
	{
		IInventory tileentitychest = (IInventory)world.getTileEntity(pos);

		if (tileentitychest != null)
		{
			for (int j1 = 0; j1 < tileentitychest.getSizeInventory(); ++j1)
			{
				ItemStack itemstack = tileentitychest.getStackInSlot(j1);

				if (itemstack != null)
				{
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					ItemEntity entityitem;

					for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.getCount() > 0; world.addEntity(entityitem))
					{
						int k1 = this.random.nextInt(21) + 10;

						if (k1 > itemstack.getCount())
						{
							k1 = itemstack.getCount();
						}

						itemstack.setCount(itemstack.getCount() - k1);

						entityitem = new ItemEntity((World) world, (double)((float)pos.getX() + f), (double)((float)pos.getY() + f1), (double)((float)pos.getZ() + f2), new ItemStack(itemstack.getItem(), k1, itemstack.getTag() ));
						float f3 = 0.05F;
						entityitem.setMotion(
								(double)((float)this.random.nextGaussian() * f3),
								(double)((float)this.random.nextGaussian() * f3 + 0.2F),
								(double)((float)this.random.nextGaussian() * f3));

						if (itemstack.hasTag())
						{
							entityitem.getItem().setTag((CompoundNBT)itemstack.getTag().copy());
						}
					}
				}
			}
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
			if(tile != null && !worldIn.isRemote) {
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tile, buf -> {buf.writeInt(((IModularInventory)tile).getModularInvType().ordinal()); buf.writeBlockPos(pos); });
			}
			return ActionResultType.SUCCESS;
	}

	/**
	 * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
	 * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
	 */
	/*public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
		TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}*/
}
