package zmaster587.libVulpes.items;

import java.util.Locale;

import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.block.INamedMetaBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemOre extends ItemBlock {

	public ItemOre(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ((INamedMetaBlock)this.getBlock()).getUnlocalizedName(stack.getItemDamage());
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
	
	@Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name") + " " + I18n.translateToLocal("type." + ((BlockOre)this.getBlock()).getProduct().name().toLowerCase(Locale.ENGLISH) + ".name")).trim();
    }
}
