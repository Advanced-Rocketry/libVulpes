package zmaster587.libVulpes.recipe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;
import zmaster587.libVulpes.recipe.RecipesMachine.DummyRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine.Recipe;

public abstract class RecipeMachineFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		
		List<List<ItemStack>> inputs = new LinkedList<List<ItemStack>>();
		List<ChanceItemStack> outputs = new LinkedList<ChanceItemStack>();
		List<FluidStack> inputFluids = new LinkedList<FluidStack>();
		List<ChanceFluidStack> outputFluids = new LinkedList<ChanceFluidStack>();
		int timeTaken = 0;
		int energy = 0;
		int maxOutput = -1;
		
		try {
			JsonElement ingredientElement = json.get("itemingredients");
	
			
			if(ingredientElement != null)
				inputs = getIngredientsFromArray(context, ingredientElement);
			
			JsonElement itemResults = json.get("itemresults");
			// Used for recipes that only can make a small number of things but may have many outputs
			JsonElement maxOutputJson = json.get("maxoutputsize");
			
			if(maxOutputJson != null)
				maxOutput = maxOutputJson.getAsInt();
			
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
			{
				for(ChanceFluidStack stack : getFluidStacks(context, fluidIngredientElement))
					inputFluids.add(stack.stack);
			}
			
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
		
		RecipesMachine.Recipe recipe = new RecipesMachine.Recipe(outputs, inputs, outputFluids, inputFluids, timeTaken, energy, new HashMap<Integer, String>());
		
		if(maxOutput > 0)
			recipe.setMaxOutputSize(maxOutput);
		
		RecipesMachine.getInstance().recipeList.get(getMachine()).add(recipe);

		// We handle our own registry
		return new DummyRecipe();
	}
	
	public abstract Class getMachine();
	
	public List<ChanceFluidStack> getFluidStacks(JsonContext context, JsonElement json)
	{
		
		if(!json.isJsonArray())
			return null;
		
		List<ChanceFluidStack> fluidstacks= new LinkedList<ChanceFluidStack>();
				
		JsonArray ingredientListJSON =  json.getAsJsonArray();
		for(JsonElement ingredient : ingredientListJSON)
		{
			fluidstacks.add(parseFluid(context, ingredient));
		}
		
		return fluidstacks;
	}
	
	public ChanceFluidStack parseFluid(JsonContext context, JsonElement json)
	{
		String fluidname = json.getAsJsonObject().get("fluid").getAsString();
		int size = json.getAsJsonObject().get("amount").getAsInt();
		JsonElement chanceElem = json.getAsJsonObject().get("chance");
		float chance = 0;
		
		if(chanceElem != null)
			chance = chanceElem.getAsFloat();
		
		return new ChanceFluidStack(new FluidStack(FluidRegistry.getFluid(fluidname),size), chance);
	}
	
	List<List<ItemStack>> getIngredientsFromArray(JsonContext context, JsonElement json)
	{
		if(!json.isJsonArray())
			//Handle error
			return null;
		
		JsonArray ingredientListJSON =  json.getAsJsonArray();
		List<List<ItemStack>> inputs = new LinkedList<List<ItemStack>>();
		for(JsonElement ingredient : ingredientListJSON)
		{
			List<ItemStack> newList = new LinkedList<ItemStack>();
			for( ChanceItemStack stack3 : getIngredients(context, ingredient) )
			{
				newList.add(stack3.stack);
			}
			inputs.add(newList);
		}
		
		return inputs;
	}
	
	List<ChanceItemStack> getIngredients(JsonContext context, JsonElement json)
	{
		List<ChanceItemStack> stacks = new LinkedList<ChanceItemStack>();
		for(ItemStack stack : CraftingHelper.getIngredient(json, context).getMatchingStacks())
		{
			int count = stack.getCount();
			int data = stack.getItemDamage();
			float chance = 0f;
			ItemStack stack2 = stack.copy();
			JsonElement countElem = json.getAsJsonObject().get("count");
			JsonElement dataElem = json.getAsJsonObject().get("data");
			JsonElement chanceElem = json.getAsJsonObject().get("chance");
			
			if(countElem != null)
				count = countElem.getAsInt();
			
			if(dataElem != null)
				data = dataElem.getAsInt();
			
			if(chanceElem != null)
				chance = chanceElem.getAsFloat();
			
			stack2.setCount(count);
			stack2.setItemDamage(data);
			stacks.add(new ChanceItemStack(stack2,chance));
		}
		return stacks;
	}

}
