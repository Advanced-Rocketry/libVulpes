package zmaster587.libVulpes.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockMetalBlock extends BlockOre {

	public BlockMetalBlock(net.minecraft.block.material.Material mat) {
		super(mat);
	}

	@Override
	public int getRenderColor(int meta) {
		if(ores[meta] != null)
			return ores[meta].getColor();
		return 0x0;
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return getRenderColor(access.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon("minecraft:iron_block");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}
}
