package zmaster587.libVulpes.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMulti extends Block {

	public BlockMulti(Material mat) {
		super(mat);
	}

	protected String[] names;
	
	
	/**
	 * @param names Array of unlocalized names for this block, also used for the texture names and lang
	 * @return self, for convenience
	 */
	public BlockMulti setNames(String[] names) {
		this.names = names;
		return this;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		state.get
		return super.damageDropped(state);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
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
