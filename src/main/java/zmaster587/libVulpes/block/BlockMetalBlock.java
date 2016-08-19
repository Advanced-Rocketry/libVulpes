package zmaster587.libVulpes.block;

import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockMetalBlock extends BlockOre {

	protected BlockMetalBlock(Material mat) {
		super(mat);
	}
	
	@Override
	public int getRenderColor(int meta) {
		if(meta > 0 && meta < MaterialRegistry.Materials.values().length )
			return MaterialRegistry.Materials.values()[meta].getColor();
		return MaterialRegistry.Materials.values()[0].getColor();
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
