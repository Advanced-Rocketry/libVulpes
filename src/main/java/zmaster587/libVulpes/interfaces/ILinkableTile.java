package zmaster587.libVulpes.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ILinkableTile {
	
	/**
	 * Called when the linker has no coords set and user rightclicks an ILinkableTile
	 * @param item Itemstack (the ItemLinker Object more likely than not)
	 * @param entity TileEntity at the coords being rightclicked
	 * @param player the player
	 * @param world the world
	 * @return true If link is allowed
	 */
	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world);
	
	public boolean onLinkComplete(ItemStack item, TileEntity entity,PlayerEntity player, World world);
}
