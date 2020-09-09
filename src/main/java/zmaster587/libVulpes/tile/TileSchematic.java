package zmaster587.libVulpes.tile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class TileSchematic extends TilePlaceholder implements ITickableTileEntity {

	private final int ttl = 6000;
	private int timeAlive = 0;
	List<BlockMeta> possibleBlocks;

	public TileSchematic() {
		super(LibVulpesTileEntityTypes.TILE_SCHEMATIC);
		possibleBlocks = new ArrayList<BlockMeta>();
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public void setReplacedBlock(List<BlockMeta> block) {
		possibleBlocks = block;
	}

	@Override
	public BlockState getReplacedState() {
		if(possibleBlocks.size() > 0 && possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getBlock() != null)
			return possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getBlockState();
			//return possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getBlock().getStateFromMeta(possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getMeta());
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public void tick() {

		if(!world.isRemote) {
			if(timeAlive == ttl) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
		timeAlive++;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putInt("timeAlive", timeAlive);

		List<Integer> blockIds = new ArrayList<Integer>();
		List<Integer> wildCard = new ArrayList<Integer>();
		for(int i = 0;  i < possibleBlocks.size();i++) {
			blockIds.add(Block.getStateId(possibleBlocks.get(i).getBlockState()));
			wildCard.add(possibleBlocks.get(i).isWild() ? 1 : 0);
		}

		if(!blockIds.isEmpty()) {
			Integer[] bufferSpace1 = new Integer[blockIds.size()];
			nbt.putIntArray("blockIds", ArrayUtils.toPrimitive(blockIds.toArray(bufferSpace1)));
			nbt.putIntArray("blockWild", ArrayUtils.toPrimitive(wildCard.toArray(bufferSpace1)));
		}

		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);
		timeAlive = nbt.getInt("timeAlive");

		if(nbt.contains("blockIds")) {
			int[] block = nbt.getIntArray("blockIds");
			int[] metas = nbt.getIntArray("blockWild");
			possibleBlocks.clear();

			for(int i = 0; i < block.length; i++) {
				if(!Block.getStateById(block[i]).isAir())
					possibleBlocks.add(new BlockMeta(Block.getStateById(block[i]), metas[i] == 1));
			}
		}
	}
}
