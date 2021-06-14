package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

//import javax.annotation.Nonnull;

public class ItemMaterialBlock extends ItemBlock {

	public ItemMaterialBlock(Block block) {
		super(block);
	}

	/*@Override
	public boolean placeBlockAt(@Nonnull ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		boolean succeeded = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, 0);

		if(succeeded) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileMaterial) {
				((TileMaterial)tile).setMaterial(Material.Materials.values()[stack.getItemDamage()]);
			}
		}

		return succeeded;
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack) {
		return this.getUnlocalizedName() + "." + getMaterial(stack).getUnlocalizedName();
	}

	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack itemStack) {
		return StatCollector.translateToLocal("material." + getMaterial(itemStack).getUnlocalizedName() + ".name") + " " + StatCollector.translateToLocal(this.getUnlocalizedName());
	}
	
	public Material.Materials getMaterial(@Nonnull ItemStack stack) {
		if(stack.getItemDamage() < 0 || stack.getItemDamage() >= Material.Materials.values().length)
			return Material.Materials.values()[0];

		return Material.Materials.values()[stack.getItemDamage()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(@Nonnull ItemStack stack, int p_82790_2_) {
		return getMaterial(stack).getColor();
	}*/

}
