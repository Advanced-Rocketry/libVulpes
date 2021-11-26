package zmaster587.libVulpes.block;

import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockOre extends Block {
	public AllowedProducts product;
	public Material material;

	public BlockOre(Properties properties, AllowedProducts product, Material material) {
		super(properties);
		this.product = product;
		this.material = material;
	}
    
	public AllowedProducts getProduct() {
		return product;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public int getHarvestLevel(BlockState state) {
		// TODO Auto-generated method stub
		return super.getHarvestLevel(state);
	}
}
