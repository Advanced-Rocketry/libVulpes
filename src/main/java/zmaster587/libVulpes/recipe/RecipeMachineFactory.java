package zmaster587.libVulpes.recipe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;

public abstract class RecipeMachineFactory extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<IRecipe> {

	public static IRecipeType<SmithingRecipe> machiningType = IRecipeType.register("machining");
	
	

	@Override
	public IRecipe read(ResourceLocation context, PacketBuffer buffer) {
		List<List<ItemStack>> inputs = new LinkedList<List<ItemStack>>();
		List<ChanceItemStack> outputs = new LinkedList<ChanceItemStack>();
		List<FluidStack> inputFluids = new LinkedList<FluidStack>();
		List<ChanceFluidStack> outputFluids = new LinkedList<ChanceFluidStack>();
		int timeTaken = 0;
		int energy = 0;
		int maxOutput = -1;
		
		// Write count
		int ingredientListSize = buffer.readInt();
		for(; ingredientListSize > 0; ingredientListSize--)
		{
			// Write subingredient count
			int subingredient = buffer.readInt();
			List<ItemStack> stacks = new LinkedList<ItemStack>();
			for(; subingredient > 0; subingredient--)
			{
				stacks.add(buffer.readItemStack());
			}
			
			inputs.add(stacks);
		}
		
		// write fluid input count
		int fluidCount = buffer.readInt();
		for(; fluidCount > 0; fluidCount--)
		{
			FluidStack stack = buffer.readFluidStack();
			inputFluids.add(stack);
		}
		
		// write item output
		int productCount = buffer.readInt();
		for(; productCount > 0; productCount--)
		{
			ItemStack stack = buffer.readItemStack();
			float chance = buffer.readFloat();
			outputs.add(new ChanceItemStack(stack, chance));
		}
		
		int fluidOutputCount = buffer.readInt();
		for(; fluidOutputCount > 0; fluidOutputCount--)
		{
			FluidStack stack = buffer.readFluidStack();
			float chance = buffer.readFloat();
			outputFluids.add(new ChanceFluidStack(stack, chance));
		}
		
		energy = buffer.readInt();
		timeTaken = buffer.readInt();
		maxOutput = buffer.readInt();
		
		RecipesMachine.Recipe recipe = new RecipesMachine.Recipe(this, context, outputs, inputs, outputFluids, inputFluids, timeTaken, energy, new HashMap<Integer, String>());
		
		if(maxOutput > 0)
			recipe.setMaxOutputSize(maxOutput);
		
		RecipesMachine.getInstance().recipeList.get(getMachine()).removeIf(value -> value.getId() == recipe.getId());
		RecipesMachine.getInstance().recipeList.get(getMachine()).add(recipe);
		
		return recipe;
	}

	@Override
	public void write(PacketBuffer buffer, IRecipe recipe) {
		
		// Write count
		buffer.writeInt(recipe.getPossibleIngredients().size());
		for(List<ItemStack> ingredientList : recipe.getPossibleIngredients())
		{
			// Write subingredient count
			buffer.writeInt(ingredientList.size());
			
			for(ItemStack ingredient : ingredientList)
			{
				buffer.writeItemStack(ingredient);
			}
		}
		
		// write fluid input count
		buffer.writeInt(recipe.getFluidIngredients().size());
		for(FluidStack fluid : recipe.getFluidIngredients() )
		{
			buffer.writeFluidStack(fluid);
		}
		
		// write item output
		buffer.writeInt(recipe.getOutput().size());
		for(ChanceItemStack product : recipe._getRawOutput())
		{
			buffer.writeItemStack(product.stack);
			buffer.writeFloat(product.chance);
		}
		
		buffer.writeInt(recipe.getFluidOutputs().size());
		for(ChanceFluidStack fluid : recipe._getRawFluidOutput() )
		{
			buffer.writeFluidStack(fluid.stack);
			buffer.writeFloat(fluid.chance);
		}
		
		buffer.writeInt(recipe.getPower());
		buffer.writeInt(recipe.getTime());
		buffer.writeInt(recipe.getRequiredEmptyOutputs());
	}
	
	@Override
	public IRecipe read(ResourceLocation context, JsonObject json) {
		
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
						outputs.addAll(getFirstIngredient(context, element));
					}
				}
				else
					outputs = getFirstIngredient(context, itemResults);
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
		
		RecipesMachine.Recipe recipe = new RecipesMachine.Recipe(this, context, outputs, inputs, outputFluids, inputFluids, timeTaken, energy, new HashMap<Integer, String>());
		
		if(maxOutput > 0)
			recipe.setMaxOutputSize(maxOutput);
		
		RecipesMachine.getInstance().recipeList.get(getMachine()).add(recipe);

		// We handle our own registry, but we need to pass it back to sync
		return recipe;
	}
	
	public abstract Class getMachine();
	
	public List<ChanceFluidStack> getFluidStacks(ResourceLocation context, JsonElement json)
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
	
	public ChanceFluidStack parseFluid(ResourceLocation context, JsonElement json)
	{
		String fluidname = json.getAsJsonObject().get("fluid").getAsString();
		int size = json.getAsJsonObject().get("amount").getAsInt();
		JsonElement chanceElem = json.getAsJsonObject().get("chance");
		float chance = 0;
		
		if(chanceElem != null)
			chance = chanceElem.getAsFloat();
		Fluid fluid = net.minecraft.util.registry.Registry.FLUID.getOrDefault(new ResourceLocation(fluidname));
		return new ChanceFluidStack(new FluidStack(fluid,size), chance);
	}
	
	List<List<ItemStack>> getIngredientsFromArray(ResourceLocation context, JsonElement json)
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
	
	List<ChanceItemStack> getIngredients(ResourceLocation context, JsonElement json)
	{
		List<ChanceItemStack> stacks = new LinkedList<ChanceItemStack>();
		for(ItemStack stack : CraftingHelper.getIngredient(json).getMatchingStacks())
		{
			int count = stack.getCount();
			int data = stack.getDamage();
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
			stack2.setDamage(data);
			stacks.add(new ChanceItemStack(stack2,chance));
		}
		return stacks;
	}

	List<ChanceItemStack> getFirstIngredient(ResourceLocation context, JsonElement json)
	{
		List<ChanceItemStack> stacks = getIngredients(context, json);
		if(stacks.size() > 1)
		{
			ChanceItemStack stack  = stacks.get(0);
			stacks = new LinkedList<ChanceItemStack>();
			stacks.add(stack);
		}
		
		
		return stacks;
	}
}
