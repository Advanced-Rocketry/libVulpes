package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.api.material.Material;

public class ItemMaterialBlock extends BlockItem {

	Material mat;
	
	public ItemMaterialBlock(Block block, Properties properties, Material mat) {
		super(block, properties);
		this.mat = mat;
	}
	
	public Material getMaterial(ItemStack stack) {
		return mat;
	}
}
