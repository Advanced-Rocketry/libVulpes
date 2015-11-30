package zmaster587.libVulpes.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipe {
	public List<ItemStack> getOutput();
	
	public List<FluidStack> getFluidOutputs();
	
	public ArrayList<ItemStack> getIngredients();
	
	public ArrayList<FluidStack> getFluidIngredients();
	
	public int getTime();
	
	public int getPower();
}
