package zmaster587.libVulpes.block.multiblock;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Invisible block used to store blocks that are part of a completed multi-block structure
 * 
 */
public class BlockMultiblockPlaceHolder extends ContainerBlock {

	VoxelShape almostFull = VoxelShapes.create(0.0001, 0.0001, 0.0001, 0.9999, 0.9999, 0.9999);
	
	public BlockMultiblockPlaceHolder() {
		super(Properties.create(Material.IRON).hardnessAndResistance(1f));
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return false;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return almostFull;
	}
	
	@Override
		public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
				ISelectionContext context) {
			return VoxelShapes.fullCube();
		}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	//Make sure to get the block this one is storing rather than the placeholder itself
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		
		TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(pos);
		return tile.getReplacedState().getPickBlock(target, world, pos, player);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		// TODO Auto-generated method stub
		return new LinkedList<ItemStack>();
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos,
			BlockState state, PlayerEntity player) {
		
		super.onBlockHarvested(world, pos, state, player);

		if(!world.isRemote && !player.isCreative()) {
			TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(pos);

			BlockState newBlockState = tile.getReplacedState();
			Block newBlock = newBlockState.getBlock();
			
			
			if(newBlock != null && newBlock != Blocks.AIR && newBlockState.canHarvestBlock(world, pos, player)) {
				newBlock.spawnDrops(newBlockState, world, pos);
			}
		}
	}
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = world.getTileEntity(pos);

		if(tile != null && tile instanceof TilePlaceholder) {
			tile = ((TilePlaceholder)tile).getMasterBlock();
			if(tile instanceof TileMultiBlock)
				((TileMultiBlock)tile).deconstructMultiBlock((World)world, pos, true, world.getBlockState(tile.getPos()));
		}
		
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TilePlaceholder();
	}
}
