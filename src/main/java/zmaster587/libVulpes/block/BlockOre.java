package zmaster587.libVulpes.block;

import java.util.List;

import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.items.ItemOreProduct;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockOre extends Block implements INamedMetaBlock, IItemColor {
	
	public zmaster587.libVulpes.api.material.Material[] ores = new zmaster587.libVulpes.api.material.Material[16];
	public static final PropertyInteger VARIANT = PropertyInteger.create("varient", 0, 15);
	public byte numBlocks;
	public AllowedProducts product;

	public BlockOre(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT,0));
	}
	
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, meta);

        return iblockstate;
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(VARIANT);
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {VARIANT});
    }
    
	public AllowedProducts getProduct() {
		return product;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < numBlocks; i++)
			if(product.isOfType(ores[i].getAllowedProducts()))
				list.add(new ItemStack(item, 1, i));
	}
	

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT);
	}

	@Override
	public String getUnlocalizedName(int itemDamage) {
		return  "material." + ores[itemDamage].getUnlocalizedName();
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		return ((BlockOre)Block.getBlockFromItem(stack.getItem())).ores[stack.getMetadata()].getColor();
	}
}
