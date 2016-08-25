package zmaster587.libVulpes.api;

import java.util.List;

import zmaster587.libVulpes.client.ResourceIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IArmorComponent {
	
	/**
	 * Called on Armor tick for each IArmorComponent in IModularArmor
	 * @param world the world
	 * @param player the player wearing the armor
	 * @param armorStack the ItemStack representing the piece of armor being worn
	 * @param modules Inventory of the armorStack containing all modules inlcuding the current one
	 * @param componentStack the ItemStack representing the current component being ticked
	 */
	public void onTick(World world, EntityPlayer player, ItemStack armorStack, IInventory modules, ItemStack componentStack);
	
	/**
	 * Called right before adding a component to the armor
	 * @param world
	 * @param player
	 * @param itemStack the armor itemStack to add the component to
	 * @return true if it should be added false otherwise
	 */
	public boolean onComponentAdded(World world, ItemStack armorStack);
	
	public void onComponentRemoved(World world, ItemStack armorStack);
	
	public void onArmorDamaged(EntityLivingBase entity, ItemStack armorStack, ItemStack componentStack, DamageSource source, int damage);
	
	public boolean isAllowedInSlot(ItemStack componentStack, int targetSlot);

	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules,
			RenderGameOverlayEvent event, Gui gui);
	
	/**
	 * @param armorStack
	 * @return The Icon for the HUD, null if none
	 */
	@SideOnly(Side.CLIENT)
	public ResourceIcon getComponentIcon(ItemStack armorStack);


}