package zmaster587.libVulpes.interfaces;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public interface IRecipe {
	public ItemStack getOutput();
	
	public ArrayList<ItemStack> getIngredients();
	
	public float getTimeMultiplyer();
	
	public float getPowerMultiplyer();
	
	public int getNumberConsumed();
}
