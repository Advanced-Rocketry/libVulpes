package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import net.minecraft.item.Item.Properties;

public class ItemBlockMeta extends  BlockItem {

	public ItemBlockMeta(Block p_i45326_1_, Properties props) {
		super(p_i45326_1_, props);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return getName();
	}
}
