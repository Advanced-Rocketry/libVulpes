package zmaster587.libVulpes.interfaces;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipe {
	public List<ItemStack> getOutput();
	
	public List<FluidStack> getFluidOutputs();
	
	public LinkedList<LinkedList<ItemStack>> getIngredients();
	
	public List<FluidStack> getFluidIngredients();
	
	public int getTime();
	
	public int getPower();
}
