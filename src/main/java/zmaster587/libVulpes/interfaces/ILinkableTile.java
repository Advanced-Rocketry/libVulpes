package zmaster587.libVulpes.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ILinkableTile {
	public void onLinkStart(ItemStack item, TileEntity entity, EntityPlayer player, World world);
	
	public void onLinkComplete(ItemStack item, TileEntity entity,EntityPlayer player, World world);
}
