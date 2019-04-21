package zmaster587.libVulpes.recipe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.DummyRecipe;

public abstract class RecipeMachineFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		
		List<List<ItemStack>> inputs = new LinkedList<List<ItemStack>>();
		List<ItemStack> outputs = new LinkedList<ItemStack>();
		List<FluidStack> inputFluids = new LinkedList<FluidStack>();
		List<FluidStack> outputFluids = new LinkedList<FluidStack>();
		int timeTaken = 0;
		int energy = 0;
		
		try {
			JsonElement ingredientElement = json.get("itemingredients");
	
			
			if(ingredientElement != null)
				inputs = getIngredientsFromArray(context, ingredientElement);
			
			JsonElement itemResults = json.get("itemresults");
			if(itemResults != null)
			{
				if (itemResults.isJsonArray())
				{
					for(JsonElement element : itemResults.getAsJsonArray())
					{
						outputs.addAll(getIngredients(context, element));
					}
				}
				else
					outputs = getIngredients(context, itemResults);
			}
			
			JsonElement fluidIngredientElement = json.get("fluidingredients");
			if(fluidIngredientElement != null)
				inputFluids = getFluidStacks(context, fluidIngredientElement);
			
			JsonElement fluidResultElement = json.get("fluidresults");
			if(fluidResultElement != null)
				 outputFluids = getFluidStacks(context, fluidResultElement);
			
			timeTaken = json.get("time").getAsInt();
			energy = json.get("energy").getAsInt();
		}
		catch(NullPointerException e)
		{
			throw new JsonParseException("Missing parameters");
		}
		
		
		RecipesMachine.getInstance().recipeList.get(getMachine()).add(new RecipesMachine.Recipe(outputs, inputs, outputFluids, inputFluids, timeTaken, energy, new HashMap<Integer, String>()));

		// We handle our own registry
		return new DummyRecipe();
	}
	
	public abstract Class getMachine();
	
	public List<FluidStack> getFluidStacks(JsonContext context, JsonElement json)
	{
		
		if(!json.isJsonArray())
			return null;
		
		List<FluidStack> fluidstacks= new LinkedList<FluidStack>();
				
		JsonArray ingredientListJSON =  json.getAsJsonArray();
		for(JsonElement ingredient : ingredientListJSON)
		{
			fluidstacks.add(parseFluid(context, ingredient));
		}
		
		return fluidstacks;
	}
	
	public FluidStack parseFluid(JsonContext context, JsonElement json)
	{
		String fluidname = json.getAsJsonObject().get("fluid").getAsString();
		int size = json.getAsJsonObject().get("amount").getAsInt();
		
		return new FluidStack(FluidRegistry.getFluid(fluidname),size);
	}
	
	List<List<ItemStack>> getIngredientsFromArray(JsonContext context, JsonElement json)
	{
		if(!json.isJsonArray())
			//Handle error
			return null;
		
		JsonArray ingredientListJSON =  json.getAsJsonArray();
		List<List<ItemStack>> inputs = new LinkedList<List<ItemStack>>();
		for(JsonElement ingredient : ingredientListJSON)
			inputs.add(getIngredients(context, ingredient));
		
		return inputs;
	}
	
	List<ItemStack> getIngredients(JsonContext context, JsonElement json)
	{
		List<ItemStack> stacks = new LinkedList<ItemStack>();
		for(ItemStack stack : CraftingHelper.getIngredient(json, context).getMatchingStacks())
		{
			int count = stack.getCount();
			int data = stack.getItemDamage();
			ItemStack stack2 = stack.copy();
			JsonElement countElem = json.getAsJsonObject().get("count");
			JsonElement dataElem = json.getAsJsonObject().get("data");
			if(countElem != null)
				count = countElem.getAsInt();
			
			if(dataElem != null)
				data = dataElem.getAsInt();
			
			stack2.setCount(count);
			stack2.setItemDamage(data);
			stacks.add(stack2);
		}
		return stacks;
	}

}
