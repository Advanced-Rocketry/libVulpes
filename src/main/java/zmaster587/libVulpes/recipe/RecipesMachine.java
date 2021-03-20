package zmaster587.libVulpes.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.TileEntityMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class RecipesMachine {
	
    public static class ChanceItemStack
    {
    	public ItemStack stack;
    	public float chance;
    	
    	public ChanceItemStack(ItemStack stack, float chance)
    	{
    		this.stack = stack;
    		this.chance = chance;
    	}
    }
    
    public static class ChanceFluidStack
    {
    	public FluidStack stack;
    	public float chance;
    	public ChanceFluidStack(FluidStack stack, float chance)
    	{
    		this.stack = stack;
    		this.chance = chance;
    	}

    }
    
	public static class Recipe implements IRecipe {

		private List<List<ItemStack>> input;
		private Map<Integer, ResourceLocation> inputOreDict;
		private LinkedList<FluidStack> fluidInput;
		private LinkedList<ChanceItemStack> output;
		private LinkedList<ChanceFluidStack> fluidOutput;
		private int completionTime, power;
		private int maxOutputSize;
		IRecipeSerializer<?> serializer;
		ResourceLocation name;

		public Recipe() {}

		public Recipe(IRecipeSerializer<?> serializer, ResourceLocation name, List<ChanceItemStack> output, List<List<ItemStack>> input, int completionTime, int powerReq, Map<Integer, ResourceLocation> oreDict) {
			this.output = new LinkedList<ChanceItemStack>();
			this.output.addAll(output);

			this.input = new LinkedList<List<ItemStack>>();
		
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;

			this.fluidInput = new LinkedList<FluidStack>();
			this.fluidOutput = new LinkedList<ChanceFluidStack>();
			
			this.inputOreDict = oreDict;
			
			maxOutputSize = -1;
			this.serializer = serializer;
			this.name = name;
		}

		public Recipe(IRecipeSerializer<?> serializer, ResourceLocation name, List<ChanceItemStack> output, List<List<ItemStack>> input, List<ChanceFluidStack> fluidOutput, List<FluidStack> fluidInput, int completionTime, int powerReq, Map<Integer, ResourceLocation> oreDict) {
			this(serializer, name, output, input, completionTime, powerReq, oreDict);

			this.fluidInput.addAll(fluidInput);
			this.fluidOutput.addAll(fluidOutput);
		}

		public void setMaxOutputSize(int size) { maxOutputSize = size; }
		
		public int getCompletionTime() {return completionTime; }


		public int getPowerReq() {
			return power;
		}

		@Override
		public List<List<ItemStack>> getPossibleIngredients() {
			return input;
		}
		
		public ResourceLocation getOreDictString(int slot) { return inputOreDict == null ? null : inputOreDict.get(slot); }

		@Override
		public List<FluidStack> getFluidIngredients() {
			return fluidInput;
		}
		
		@Override
		public List<FluidStack> getFluidOutputs() {
			List<FluidStack> stacks = new LinkedList<FluidStack>();
			fluidOutput.forEach((ChanceFluidStack s) -> { stacks.add(s.stack); });
			return stacks;
		}

		@Override
		public int getTime() {
			return completionTime;
		}

		@Override
		public int getPower() {
			return power;
		}

		@Override
		public List<ChanceItemStack> _getRawOutput()
		{
			return output;
		}
		
		@Override
		public List<ChanceFluidStack> _getRawFluidOutput()
		{
			return fluidOutput;
		}
		
		@Override
		public List<ItemStack> getOutput() {
			ArrayList<ItemStack> stack = new ArrayList<ItemStack>();

			int maxOutputSize = getRequiredEmptyOutputs();
			
			if(maxOutputSize > 0)
			{
				float maxChance = 0;
				Random rand = new Random(System.currentTimeMillis());
				for(ChanceItemStack i : output) {
					maxChance += i.chance;
				}
				
				for(int i = 0; i < maxOutputSize; i++)
				{
					float currentHit = rand.nextFloat()*maxChance;
					float currChance = 0;
					ItemStack nextStack = output.get(0).stack;
					for(ChanceItemStack currStack : output) {
						currChance += currStack.chance;
						if(currChance > currentHit)
						{
							stack.add(nextStack.copy());
							break;
						}
						nextStack = currStack.stack;
					}
				}
			}
			else
			{
				for(ChanceItemStack i : output) {
					stack.add(i.stack.copy());
				}
			}

			if(stack.contains(null))
			{
				while(stack.remove(null));
				
				LibVulpes.logger.warn("Recipe " + this.getId().toString() + " has null item output, this should NOT happen!!");
			}
			
			return stack;
		}
		
		public List<ChanceItemStack> getChanceOutputs()
		{
			return output;
		}

		public int getRequiredEmptyOutputs()
		{
			return maxOutputSize;
		}

		public boolean outputToOnlyEmptySlots()
		{
			return maxOutputSize != -1;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Recipe) {
				Recipe otherRecipe = (Recipe)obj;
				if(input.size() != otherRecipe.input.size() || fluidInput.size() != otherRecipe.fluidInput.size())
					return false;

				for(int i = 0; i < input.size(); i++) {
					if(input.get(i).size() != otherRecipe.input.get(i).size())
						return false;
					for(int j = 0; j < input.get(i).size(); j++) {
						if(!ItemStack.areItemStacksEqual(input.get(i).get(j), otherRecipe.input.get(i).get(j)))
							return false;
					}
				}

				for(int i = 0; i < fluidInput.size(); i++) {
					if(fluidInput.get(i).getFluid() != otherRecipe.fluidInput.get(i).getFluid())
						return false;
				}
			}

			return true;
		}

		@Override
		public boolean matches(IInventory inv, World worldIn) {
			// never match, we handle this in the machines
			return false;
		}

		@Override
		public ItemStack getCraftingResult(IInventory inv) {
			// do not use
			return null;
		}

		@Override
		public boolean canFit(int width, int height) {
			// handled in machine
			return false;
		}

		@Override
		public ItemStack getRecipeOutput() {
			// used by vanilla :/
			return ItemStack.EMPTY;
		}

		@Override
		public ResourceLocation getId() {
			return name;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return serializer;
		}

		@Override
		public IRecipeType<?> getType() {
			return RecipeMachineFactory.machiningType;
		}
	}

	public HashMap<Class<?>, List<IRecipe>> recipeList;

	private static RecipesMachine instance = new RecipesMachine();

	public RecipesMachine() {
		recipeList = new HashMap<Class<?>, List<IRecipe>>();
	}

	public static RecipesMachine getInstance() { return instance; }

	public void clearRecipes(Class<? extends TileEntityMachine> clazz) {
		recipeList.get(clazz).clear();
	}
	
	public void addRecipe(ResourceLocation name, IRecipeSerializer<?> serializer, Class clazz , Object[] out, int timeRequired, int power, Object ... inputs) {
		List<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new LinkedList<IRecipe>();
			recipeList.put(clazz,recipes);
		}

		Map<Integer, ResourceLocation> oreDict = new HashMap<Integer, ResourceLocation>();
		LinkedList<List<ItemStack>> stack = new LinkedList<List<ItemStack>>();

		ArrayList<FluidStack> inputFluidStacks = new ArrayList<FluidStack>();

		try {
			for(int i = 0; i < inputs.length; i++) {
				LinkedList<ItemStack> innerList = new LinkedList<ItemStack>();
				if(inputs[i] != null) {
					if(inputs[i] instanceof String) {
						ResourceLocation res = new ResourceLocation((String)inputs[i]);
						oreDict.put(i, res);
						for (Item item : ItemTags.getCollection().get(res).getAllElements() ) {
							innerList.add(new ItemStack(item));
						}
					}
					else if(inputs[i] instanceof ResourceLocation) {
						oreDict.put(i, ((ResourceLocation)inputs[i]));
						if(!ItemTags.getCollection().getRegisteredTags().contains((ResourceLocation)inputs[i]))
							throw new  NullPointerException("No such item tag registered: " + ((ResourceLocation)inputs[i]).toString());
						
						for (Item item : ItemTags.getCollection().get((ResourceLocation)inputs[i]).getAllElements() ) {
							innerList.add(new ItemStack(item));
						}
					}
					else if(inputs[i] instanceof NumberedOreDictStack) {
						oreDict.put(i, ((NumberedOreDictStack)inputs[i]).ore);
						for (Item item : ItemTags.getCollection().get(new ResourceLocation((String)inputs[i])).getAllElements()) {
							int number  = ((NumberedOreDictStack)inputs[i]).getNumber();
							ItemStack stack2 = new ItemStack(item, number);
							stack2.setCount(number);
							innerList.add(stack2);
						}
					}
					else if(inputs[i] instanceof FluidStack)
						inputFluidStacks.add((FluidStack) inputs[i]);
					else {

						if(inputs[i] instanceof Item) 
							inputs[i] = new ItemStack((Item)inputs[i]);
						else if(inputs[i] instanceof Block)
							inputs[i] = new ItemStack((Block)inputs[i]);

						innerList.add((ItemStack)inputs[i]);
					}
				}
				if(!innerList.isEmpty())
				stack.add(innerList);
			}
			ArrayList<ChanceItemStack> outputItem = new ArrayList<ChanceItemStack>();
			ArrayList<ChanceFluidStack> outputFluidStacks = new ArrayList<ChanceFluidStack>();

			for(Object outputObject : out) {
				if(outputObject instanceof ItemStack)
					outputItem.add(new ChanceItemStack((ItemStack)outputObject, 0f));
				else
					outputFluidStacks.add(new ChanceFluidStack((FluidStack)outputObject, 0f));
			}

			Recipe recipe;
			if(inputFluidStacks.isEmpty() && outputFluidStacks.isEmpty())
				recipe = new Recipe(serializer, name, outputItem, stack, timeRequired, power, oreDict);
			else
				recipe = new Recipe(serializer, name, outputItem, stack, outputFluidStacks, inputFluidStacks, timeRequired, power, oreDict);

			if(recipes.contains(recipe)) 
				LibVulpes.logger.info("Overwriting recipe " + recipes.remove(recipe));
			recipes.add(recipe);

		} catch(ClassCastException e) {
			//Custom handling to make sure it logs and can be suppressed by user
			String message = e.getLocalizedMessage();

			for(StackTraceElement element : e.getStackTrace()) {
				message += "\n\t" + element.toString();
			}

			LibVulpes.logger.warn("Cannot add recipe!");
			LibVulpes.logger.warn(message);

		}
		catch(NullPointerException e)
		{
			//Custom handling to make sure it logs and can be suppressed by user
			String message = e.getLocalizedMessage();

			for(StackTraceElement element : e.getStackTrace()) {
				message += "\n\t" + element.toString();
			}

			LibVulpes.logger.warn("Cannot add recipe!");
			LibVulpes.logger.warn(message);
		}
	}

	/**
	 * @param clazz Class object of the machine to register the recipe
	 * @param out outout object of the machine, accepts itemStack and fluidStacks
	 * @param timeRequired base running time for the recipe in ticks
	 * @param power power units per tick
	 * @param inputs input objects for the recipe, accepts forge ore dict entries as strings, itemStacks, Items, Blocks, and fluidStacks
	 */
	public void addRecipe(ResourceLocation name, IRecipeSerializer<?> serializer, Class clazz , Object out, int timeRequired, int power, Object ... inputs) {
		addRecipe(name, serializer, clazz, new Object[] {out}, timeRequired, power, inputs);
	}
	
	public void addRecipe(ResourceLocation name, IRecipeSerializer<?> serializer, Class clazz , List<Object> out, int timeRequired, int power, List<Object> inputs) {
		
		Object outputs[] = new Object[out.size()];
		outputs = out.toArray(outputs);
		
		Object inputs2[] = new Object[inputs.size()];
		inputs2 = inputs.toArray(inputs2);
		
		addRecipe(name, serializer, clazz, outputs, timeRequired, power, inputs2);
	}

	//Given the class return the list
	public List<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}