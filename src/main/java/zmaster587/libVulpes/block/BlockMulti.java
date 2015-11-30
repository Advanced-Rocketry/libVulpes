package zmaster587.libVulpes.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockMulti extends Block {

	public BlockMulti(Material mat) {
		super(mat);
	}

	protected String[] names;
	protected IIcon[] textures;
	
	
	/**
	 * @param names Array of unlocalized names for this block, also used for the texture names and lang
	 * @return self, for convenience
	 */
	public BlockMulti setNames(String[] names) {
		this.names = names;
		return this;
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata)
	{
		return textures[metadata];
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
    	textures = new IIcon[names.length];
    	for(int i = 0; i < names.length; i++) {
    		textures[i] = reg.registerIcon(names[i]);
    	}
    }
    
    @Override 
    public int getDamageValue(World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1, CreativeTabs tab, List subItems) {
		for (int ix = 0; ix < names.length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

}
