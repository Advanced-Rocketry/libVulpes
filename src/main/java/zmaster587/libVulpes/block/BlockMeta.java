package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockMeta {
	BlockState block;
	boolean wildcard;
	public static final boolean WILDCARD = true;

	public BlockMeta(BlockState block, boolean wildcard) {
		this.block = block;
		this.wildcard = wildcard;
	}


	public BlockMeta(BlockState block) {
		this.block = block;
		this.wildcard = false;
	}
	
	public BlockMeta(Block block) {
		this.block = block.getDefaultState();
		this.wildcard = false;
	}
	
	public BlockMeta(Block block, boolean wildcard) {
		this.block = block.getDefaultState();
		this.wildcard = wildcard;
	}
	
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof BlockMeta) {
			return ((BlockMeta)obj).block.getBlock() == block.getBlock() && ( wildcard || ((BlockMeta)obj).wildcard || ((BlockMeta)obj).block == block);
		}
		return super.equals(obj);
	}

	public Block getBlock() {
		return block.getBlock();
	}
	
	@Override
	public int hashCode() {
		return block.hashCode();
	}
	
	public BlockState getBlockState() {
		
		if(wildcard) {
			return block.getBlock().getDefaultState();
		}
		return block;
	}


	public Boolean isWild() {
		return wildcard;
	}
}
