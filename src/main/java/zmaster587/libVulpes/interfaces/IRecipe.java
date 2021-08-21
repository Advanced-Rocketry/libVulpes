package zmaster587.libVulpes.interfaces;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine;

public interface IRecipe extends net.minecraft.item.crafting.IRecipe<IInventory> {
	List<ItemStack> getOutput();
	
	List<FluidStack> getFluidOutputs();

	List<List<ItemStack>> getPossibleIngredients();
	
	List<FluidStack> getFluidIngredients();
	
	int getTime();
	
	int getPower();

	ResourceLocation getOreDictString(int slot);

	List<RecipesMachine.ChanceItemStack> _getRawOutput();
	List<RecipesMachine.ChanceFluidStack> _getRawFluidOutput();

	int getRequiredEmptyOutputs();
}
