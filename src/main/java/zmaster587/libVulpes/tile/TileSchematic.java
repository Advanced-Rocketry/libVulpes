package zmaster587.libVulpes.tile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class TileSchematic extends TilePlaceholder implements ITickable {

	private final int ttl = 6000;
	private int timeAlive = 0;
	List<BlockMeta> possibleBlocks;

	public TileSchematic() {
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
	public int getBlockMetadata() {
		if(possibleBlocks.size() == 0)
			return 0;
		return possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getMeta();
	}

	@Override
	public IBlockState getReplacedState() {
		if(possibleBlocks.size() > 0 && possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getBlock() != null)
			return possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getBlock().getStateFromMeta(possibleBlocks.get((timeAlive / 20) % possibleBlocks.size()).getMeta());
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public void update() {

		if(!world.isRemote) {
			if(timeAlive == ttl) {
				world.setBlockToAir(pos);
			}
		}
		timeAlive++;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("timeAlive", timeAlive);

		List<Integer> blockIds = new ArrayList<Integer>();
		List<Integer> blockMetas = new ArrayList<Integer>();
		for(int i = 0;  i < possibleBlocks.size();i++) {
			blockIds.add(Block.getIdFromBlock(possibleBlocks.get(i).getBlock()));
			blockMetas.add((int)possibleBlocks.get(i).getMeta());
		}

		if(!blockIds.isEmpty()) {
			Integer[] bufferSpace1 = new Integer[blockIds.size()];
			Integer[] bufferSpace2 = new Integer[blockIds.size()];
			nbt.setIntArray("blockIds", ArrayUtils.toPrimitive(blockIds.toArray(bufferSpace1)));
			nbt.setIntArray("blockMetas", ArrayUtils.toPrimitive(blockMetas.toArray(bufferSpace2)));
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		timeAlive = nbt.getInteger("timeAlive");

		if(nbt.hasKey("blockIds")) {
			int[] block = nbt.getIntArray("blockIds");
			int[] metas = nbt.getIntArray("blockMetas");
			possibleBlocks.clear();

			for(int i = 0; i < block.length; i++) {
				if(Block.getBlockById(block[i]) != Blocks.AIR)
					possibleBlocks.add(new BlockMeta(Block.getBlockById(block[i]), metas[i]));
			}
		}
	}
}
