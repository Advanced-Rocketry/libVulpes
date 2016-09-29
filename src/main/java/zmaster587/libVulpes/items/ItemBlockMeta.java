package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends  ItemBlock {

	public ItemBlockMeta(Block p_i45326_1_) {
		super(p_i45326_1_);
		
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}
	

	@Override
	public int getMetadata(int damage) {
		return damage & 15;
	}
	
}
