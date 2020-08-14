package zmaster587.libVulpes.block.multiblock;

import java.util.List;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
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

/**
 * hosts a multiblock machine master tile
 *
 */
public class BlockMultiblockMachine extends BlockTile {

	
	
	public BlockMultiblockMachine(AbstractBlock.Properties property, TileEntityType<?> tileClass,
			int guiId) {
		super(property, guiId);
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)  {
		

		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete())
				tileMulti.deconstructMultiBlock((World)world, pos, false, state);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn)  {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		Style style = Style.field_240709_b_.func_240712_a_(TextFormatting.DARK_GRAY).func_240712_a_(TextFormatting.ITALIC);
		
		tooltip.add( new TranslationTextComponent("machine.tooltip.multiblock").func_240703_c_(style));
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		/*BlockPos pos = pos2;//pos2.offset(direction.getOpposite());
		
		TileEntity tile = access.getTileEntity(pos2);
		if(tile instanceof TileMultiBlock) {
			return !((TileMultiBlock)tile).shouldHideBlock(tile.getWorld(), pos, access.getBlockState(pos)) || !((TileMultiBlock)tile).canRender();
		}*/
		
		return super.isSideInvisible(state, adjacentBlockState, side);
	}
	
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete() && !worldIn.isRemote) {
				if(tile instanceof IModularInventory)
					NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileMulti, pos);
			}
			else
				return tileMulti.attemptCompleteStructure(state) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
		return ActionResultType.SUCCESS;
	}
}
