package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockMeta {
	Block block;
	int meta;
	public static final int WILDCARD = -1;

	public BlockMeta(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}


	public BlockMeta(Block block) {
		this.block = block;
		this.meta = WILDCARD;
	}
	
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof BlockMeta) {
			return ((BlockMeta)obj).block == block && ( meta == -1 || ((BlockMeta)obj).meta == -1 || ((BlockMeta)obj).meta == meta);
		}
		return super.equals(obj);
	}

	public Block getBlock() {
		return block;
	}
	
	@Override
	public int hashCode() {
		return block.hashCode() + meta;
	}
	
	public IBlockState getBlockState() {
		if(meta != WILDCARD) {
			return block.getStateFromMeta(meta);
		}
		return block.getDefaultState();
	}

	public byte getMeta() {
		if(meta != WILDCARD)
			return (byte) meta;
		return 0;
	}
}
