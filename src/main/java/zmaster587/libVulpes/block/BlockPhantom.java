package zmaster587.libVulpes.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class BlockPhantom extends Block {

	public BlockPhantom(Properties mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		return new ArrayList<>();
	}

	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileSchematic();
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof TilePlaceholder && ((TilePlaceholder) tile).getReplacedState() != null) {
			Block block = ((TilePlaceholder)tile).getReplacedState().getBlock();


			return ((TilePlaceholder)tile).getReplacedState().getBlock().getPickBlock(((TilePlaceholder)tile).getReplacedState(), target, world, pos, player);
		}
		return super.getPickBlock(state, target, world, pos, player);
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return true;
	}
	
	
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return true;
	}
	
	@Override
	public boolean isReplaceable(BlockState p_225541_1_, Fluid p_225541_2_) {
		return true;
	}
	
	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return VoxelShapes.empty();
	}
}
