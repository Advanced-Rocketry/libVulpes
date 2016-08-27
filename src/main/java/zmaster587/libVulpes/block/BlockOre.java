package zmaster587.libVulpes.block;

import java.util.List;

import zmaster587.libVulpes.api.material.AllowedProducts;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockOre extends Block implements INamedMetaBlock {
	
	public zmaster587.libVulpes.api.material.Material[] ores = new zmaster587.libVulpes.api.material.Material[16];
	IIcon[] textures = new IIcon[16];
	public byte numBlocks;
	public AllowedProducts product;

	public BlockOre(Material material) {
		super(material);
	}

	public AllowedProducts getProduct() {
		return product;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return textures[meta];
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < numBlocks; i++)
			if(product.isOfType(ores[i].getAllowedProducts()))
				list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		for(int i = 0; i < numBlocks; i++) {
			if(product.isOfType(ores[i].getAllowedProducts()) )
				textures[i] = iconRegister.registerIcon("libvulpes:" + textureName + ores[i].getUnlocalizedName());
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public String getUnlocalizedName(int itemDamage) {
		return  "material." + ores[itemDamage].getUnlocalizedName();
	}
}
