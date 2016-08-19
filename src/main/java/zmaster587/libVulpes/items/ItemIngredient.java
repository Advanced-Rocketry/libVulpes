package zmaster587.libVulpes.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemIngredient extends Item {
	
	private int numIngots;
	
	public ItemIngredient(int num) {
		super();
		numIngots = num;
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	private IIcon[] icons;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < numIngots; i++) {
				itemList.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().split(":")[1];
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append("item." + super.getUnlocalizedName().split(":")[1]).append(".").append(itemstack.getItemDamage()).toString();
	}
	
	@Override
	public IIcon getIconFromDamage(int i) {
		return i < icons.length ? icons[i] : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[numIngots];
		String fullName = super.getUnlocalizedName().replaceFirst("item\\.", "");
		String namespace = fullName.split(":")[0];
		fullName = fullName.split(":")[1];
		
		for (int i = 0; i < numIngots; i++) {
			icons[i] = par1IconRegister.registerIcon(namespace + ":" + fullName + i);
		}
	}

	public void registerItemStacks() {
		for(int i = 0; i < numIngots; i++) {
			ItemStack stack = new ItemStack(this, 1, i);
			GameRegistry.registerCustomItemStack(getUnlocalizedName(stack), stack);
		}
	}
}
