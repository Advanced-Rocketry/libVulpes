package zmaster587.libVulpes.block.multiblock;

import java.util.List;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
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
public class BlockMultiblockMachine extends BlockTile implements IHidableBlock {

	public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
	
	public BlockMultiblockMachine(AbstractBlock.Properties property,
			GuiHandler.guiId guiId) {
		super(property, guiId);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(VISIBLE);
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
	
	public void hideBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VISIBLE, false));
	}

	public void showBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(VISIBLE, true));
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
		return super.isSideInvisible(state, adjacentBlockState, side) || !state.get(VISIBLE);
	}
	 @Override
	public boolean isVariableOpacity() {
		return true;
	}
	 
	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.get(VISIBLE) ? state.getShape(worldIn, pos) : VoxelShapes.empty();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete() && !worldIn.isRemote) {
				if(tile instanceof IModularInventory)
					NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileMulti, buf -> {buf.writeInt(((IModularInventory)tile).getModularInvType().ordinal()); buf.writeBlockPos(pos); });
			}
			else
				// Needs to run on client to integrate tiles
				return tileMulti.attemptCompleteStructure(state) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
		return ActionResultType.SUCCESS;
	}
}
