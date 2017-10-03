package zmaster587.libVulpes.items;

import java.util.List;

import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIngredient extends Item
{

    private int numIngots;

    public ItemIngredient(int num)
    {
        super();
        numIngots = num;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> itemList)
    {
        if (!this.isInCreativeTab(par2CreativeTabs)) return;
        for (int i = 0; i < numIngots; i++)
        {
            itemList.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName()
    {
        return super.getUnlocalizedName().split(":")[1];
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return (new StringBuilder()).append("item." + super.getUnlocalizedName().split(":")[1]).append(".")
                .append(itemstack.getItemDamage()).toString();
    }
}
