package zmaster587.libVulpes.block;

import net.minecraft.block.Block;

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

	public int getMeta() {
		if(meta != WILDCARD)
			return meta;
		return 0;
	}
}
