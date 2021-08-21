package zmaster587.libVulpes.api;

import zmaster587.libVulpes.client.ResourceIcon;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	public void onTick(World world, PlayerEntity player, ItemStack armorStack, IInventory modules, ItemStack componentStack);

	/**
	 * Called right before adding a component to the armor
	 * @param world
	 * @param player
	 * @param itemStack the armor itemStack to add the component to
	 * @return true if it should be added false otherwise
	 */
	public boolean onComponentAdded(World world, ItemStack armorStack);

	public void onComponentRemoved(World world, ItemStack armorStack);

	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack, ItemStack componentStack, DamageSource source, int damage);

	public boolean isAllowedInSlot(ItemStack componentStack, EquipmentSlotType armorType);

	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules,
							 RenderGameOverlayEvent event, Screen gui);

	/**
	 * @param armorStack
	 * @return The Icon for the HUD, null renders standard item
	 */
	@OnlyIn(value=Dist.CLIENT)
	public ResourceIcon getComponentIcon(ItemStack armorStack);


}