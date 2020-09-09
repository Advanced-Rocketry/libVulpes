package zmaster587.libVulpes.interfaces;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;

public interface IRecipe extends net.minecraft.item.crafting.IRecipe<IInventory> {
	public List<ItemStack> getOutput();
	
	public List<FluidStack> getFluidOutputs();
	
	public List<List<ItemStack>> getPossibleIngredients();
	
	public List<FluidStack> getFluidIngredients();
	
	public int getTime();
	
	public int getPower();
	
	public ResourceLocation getOreDictString(int slot);

	List<ChanceItemStack> _getRawOutput();
	List<ChanceFluidStack> _getRawFluidOutput();

	public int getRequiredEmptyOutputs();
}
