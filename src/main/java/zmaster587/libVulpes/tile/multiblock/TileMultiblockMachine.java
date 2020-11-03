package zmaster587.libVulpes.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.ZUtils;

public abstract class TileMultiblockMachine extends TileMultiPowerConsumer {

	public enum NetworkPackets {
		TOGGLE,
		POWERERROR
	}

	private List<ItemStack> outputItemStacks;
	private List<FluidStack> outputFluidStacks;

	boolean smartInventoryUpgrade = true;
	//When using smart inventories sometimes setInventory content calls need to be made
	//This flag prevents infinite recursion by having a value of true if any invCheck has started
	boolean invCheckFlag = false;

	public TileMultiblockMachine(TileEntityType<?> type) {
		super(type);
		outputItemStacks = null;
	}

	public List<ItemStack> getOutputs() {
		return outputItemStacks;
	}
	
	public void setOutputs(List<ItemStack> stack) {
		outputItemStacks = stack;
	}
	
	public void setOutputFluids(List<FluidStack> stack) {
		outputFluidStacks = stack;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();

		write(nbt);
		nbt.putBoolean("built", canRender);
		nbt.putBoolean("hadPowerLastTick", hadPowerLastTick);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}
	
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = write(new CompoundNBT());
		nbt.putBoolean("built", canRender);
		nbt.putBoolean("hadPowerLastTick", hadPowerLastTick);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		super.handleUpdateTag(state, nbt);
		canRender = nbt.getBoolean("built");
		hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		canRender = nbt.getBoolean("built");
		hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		deserializeNBT(nbt);
	}

	public void registerRecipes() {
		
	}
	
	@Override
	public void tick() {
		//super.update();

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0) {
			if(!world.isRemote) {
				if(isComplete())
					canRender = completeStructure = completeStructure(world.getBlockState(pos));
			}
			else {
				SoundEvent str;
				if(world.isRemote && (str = getSound()) != null) {
					playMachineSound(str);
				}
			}

			timeAlive = 0x1;
		}

		//In case the machine jams for some reason
		if(!isRunning() && world.getGameTime() % 1000L == 0)
			onInventoryUpdated();

		if(isRunning()) {
			if( hasEnergy(powerPerTick) || (world.isRemote && hadPowerLastTick)) {

				//Increment for both client and server
				onRunningPoweredTick();
				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!world.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
					}

					useEnergy(powerPerTick);
				}
			}
			else if(!world.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
			}
		}
	}

	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);
		onInventoryUpdated();
	}

	@Override
	public void resetCache() {
		super.resetCache();
		batteries.clear();
	}

	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, BlockState state) {
		outputItemStacks = null;
		outputFluidStacks = null;
		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;
		if(!world.isRemote)
			dumpOutputToInventory();

		outputItemStacks = null;
		outputFluidStacks = null;

		onInventoryUpdated();

		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	//When the output of the recipe is dumped to the inventory
	protected void dumpOutputToInventory() {

		int totalItems = 0;
		for(IInventory outInventory : getItemOutPorts()) {
			for(int i = totalItems; i < outputItemStacks.size(); i++) {
				ItemStack stack = outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i);

				if(stack == ItemStack.EMPTY || stack.getItem() == Items.AIR) {
					outInventory.setInventorySlotContents(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i, outputItemStacks.get(i));
					outInventory.markDirty();
					totalItems++;
				}
				else if(stack.isItemEqual(outputItemStacks.get(i)) && 
						(stack.getCount() + outputItemStacks.get(i).getCount() <= outInventory.getInventoryStackLimit() && stack.getCount() + outputItemStacks.get(i).getCount() <= stack.getMaxStackSize())) {
					outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).setCount(outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).getCount() + outputItemStacks.get(i).getCount());
					outInventory.markDirty();
					totalItems++;
				}
			}
		}

		//Handle fluids
		for(int i = 0; i < outputFluidStacks.size() ; i++) {
			fluidOutPorts.get(i).fillInternal(outputFluidStacks.get(i), FluidAction.EXECUTE);
		}
	}

	//TODO: improve recipe checks
	//Attempt to get a valid recipe given the inputs, null if none found
	protected IRecipe getRecipe(List<IRecipe> set) {

		for(IRecipe recipe : set) {

			if(canProcessRecipe(recipe))
				return recipe;

		}
		return null;
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of itemstacks the machine can output
	 */
	protected List<ItemStack> getItemOutputs(IRecipe recipe) {
		return recipe.getOutput();
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of fluidstacks the machine can output
	 */
	protected List<FluidStack> getFluidOutputs(IRecipe recipe) {
		return recipe.getFluidOutputs();
	}


	public void consumeItems(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getPossibleIngredients();

		for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

			List<ItemStack> ingredient = ingredients.get(ingredientNum);

			ingredientCheck:

				for(IInventory hatch : getItemInPorts()) {
					for(int i = 0; i < hatch.getSizeInventory(); i++) {
						ItemStack stackInSlot = hatch.getStackInSlot(i);

						for (ItemStack stack : ingredient) {
							if(stackInSlot != null && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) || (/*stack.getDamage() == OreDictionary.WILDCARD_VALUE &&*/ stackInSlot.getItem() == stack.getItem() ))) {
								hatch.decrStackSize(i, stack.getCount());
								hatch.markDirty();
								world.notifyBlockUpdate(pos, world.getBlockState(((TileEntity)hatch).getPos()),  world.getBlockState(((TileEntity)hatch).getPos()), 6);
								break ingredientCheck;
							}
						}
					}
				}
		}


		//Consume fluids
		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			fluidInputCounter[i] = recipe.getFluidIngredients().get(i).getAmount();
		}

		//Drain Fluid containers
		for(IFluidHandlerInternal fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = recipe.getFluidIngredients().get(i).copy();
				fluidStack.setAmount(fluidInputCounter[i]);

				FluidStack drainedFluid;
				drainedFluid = fluidInput.drainInternal(recipe.getFluidIngredients().get(i), FluidAction.EXECUTE);

				if(drainedFluid != null)
					fluidInputCounter[i] -= drainedFluid.getAmount();

			}
		}
	}

	//Can this recipe be processed
	public boolean canProcessRecipe(IRecipe recipe) {

		if( !isComplete() || invCheckFlag)
			return false;

		invCheckFlag = true;
		int reservedSpotSize = 0;
		if(recipe instanceof RecipesMachine.Recipe)
			reservedSpotSize = ((RecipesMachine.Recipe)recipe).getRequiredEmptyOutputs();
		
		List<ItemStack> outputItems = getItemOutputs(recipe);

		boolean itemCheck = outputItems.size() == 0;


		List<List<ItemStack>> ingredients = recipe.getPossibleIngredients();
		short mask = 0x0;
		recipeCheck:

			for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

				List<ItemStack> ingredient = ingredients.get(ingredientNum);
				ingredientCheck:

					for(IInventory hatch : getItemInPorts()) {

						for(int i = 0; i < hatch.getSizeInventory(); i++) {
							ItemStack stackInSlot = hatch.getStackInSlot(i);

							for(ItemStack stack : ingredient) {
								if(stackInSlot != ItemStack.EMPTY && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) || (/*stack.getDamage() == OreDictionary.WILDCARD_VALUE &&*/ stack.getItem() == stackInSlot.getItem()))) {
									mask |= (1 << ingredientNum);
									break ingredientCheck;
								}
							}
						}

						//If no matching item is found for the ingredient
						//break recipeCheck;
					}


			}
		if(mask != (1 << ( ( ingredients.size() ) )) - 1) {
			invCheckFlag = false;
			return false;
		}


		if(reservedSpotSize < 0)
		{
		//Check output Items
		bottomItemCheck:
			for(IInventory outInventory : getItemOutPorts()) {
				//It's possible for the outputItems to be bigger than the inventory
				if(outInventory.getSizeInventory() - outputItems.size() < 0)
					continue;
				
				for(int i = smartInventoryUpgrade ? outInventory.getSizeInventory() - outputItems.size() : 0; (i < (smartInventoryUpgrade ? outInventory.getSizeInventory() : outputItems.size())); i++) {
					ItemStack stack = outInventory.getStackInSlot(i);

					if(smartInventoryUpgrade) {
						ItemStack outputItem = outputItems.get(outInventory.getSizeInventory() - i - 1);


						boolean allIngredFit = true;
						for(int k = 0; k < outputItems.size() && i - k >= 0; k++) {
							ItemStack stack2 = outInventory.getStackInSlot(outInventory.getSizeInventory()-k-1);
							ItemStack outputItem2  = outputItems.get(k);
							allIngredFit = stack2.isEmpty() || (stack2.isItemEqual(outputItem2) && stack2.getCount() + outputItem2.getCount() <= outInventory.getInventoryStackLimit() && stack2.getCount() + outputItem2.getCount() <= stack.getMaxStackSize());
							
							if(!allIngredFit) break;
						}
						
						//stack cannot be null when assigning flag
						if(allIngredFit) {
							
							//Check all the slots
							invCheckFlag = false;
							itemCheck = true;
							break bottomItemCheck;
						}

						if(stack != ItemStack.EMPTY && ZUtils.getFirstFilledSlotIndex(outInventory) >= outputItems.size()) {
							//Range Check
							int outputSize = outputItems.size();
							int j;
							for(j = 0; j < outputSize; j++) {
								if(outInventory.getStackInSlot(j) != ItemStack.EMPTY) {
									invCheckFlag = false;
									itemCheck = false;
									break bottomItemCheck;
								}
							}

							int numExtraMoves = outInventory.getSizeInventory() - ZUtils.getFirstFilledSlotIndex(outInventory) - 1;

							//J will be last slot in index by now
							for(j = outInventory.getSizeInventory() - outputSize; j > 0; j--) {
								int slot = outInventory.getSizeInventory();
								outInventory.setInventorySlotContents(slot - j - outputSize, outInventory.getStackInSlot(slot - j));
								outInventory.setInventorySlotContents(slot - j, ItemStack.EMPTY);
							}

							invCheckFlag = false;
							itemCheck = true;
							break bottomItemCheck;
						}
					}
					else if(stack == ItemStack.EMPTY || stack.isItemEqual(outputItems.get(i)) && 
							(stack.getCount() + outputItems.get(i).getCount() <= outInventory.getInventoryStackLimit() && stack.getCount() + outputItems.get(i).getCount() <= stack.getMaxStackSize())) {
						itemCheck = true;
						break bottomItemCheck;
					}

				}
			}
		}
		else //Chance output
		{
			for(IInventory outInventory : getItemOutPorts()) {
				for(int i = 0; i < outInventory.getSizeInventory(); i++) {
					if(outInventory.getStackInSlot(i).isEmpty())
						reservedSpotSize--;
				}
			}
			
			if(reservedSpotSize <= 0)
				itemCheck = true;
		}

		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		//Populate Fluid Counters
		for(IFluidHandlerInternal fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = fluidInput.drainInternal(recipe.getFluidIngredients().get(i), FluidAction.SIMULATE);

				if(fluidStack != null)
					fluidInputCounter[i] += fluidStack.getAmount();
			}
		}

		invCheckFlag = false;
		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			if(fluidInputCounter[i] < recipe.getFluidIngredients().get(i).getAmount())
				return false;
		}

		//Check outputs


		if(fluidOutPorts.size() < recipe.getFluidOutputs().size())
			return false;

		int[] fluidOutputCounter = new int[recipe.getFluidOutputs().size()];

		//Populate the list
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] = recipe.getFluidOutputs().get(i).getAmount();
		}

		//Populate Fluid Counters
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] -= fluidOutPorts.get(i).fillInternal(recipe.getFluidOutputs().get(i), FluidAction.EXECUTE);
		}

		for(int i = 0; i < fluidOutputCounter.length; i++)
			if(fluidOutputCounter[i] > 0 )
				return false;

		return itemCheck;
	}


	//Used To make sure the multiblock is valid
	/*@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();

		completeStructure = completeStructure();
	}*/


	//Must be overridden or an NPE will occur
	public List<IRecipe> getMachineRecipeList() {
		List<IRecipe> list = RecipesMachine.getInstance().getRecipes(this.getClass());
		return (list != null) ? list : new LinkedList<IRecipe>();
	}

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		//If we are already processing something don't bother

		if(!invCheckFlag && outputItemStacks == null && (outputFluidStacks == null || outputFluidStacks.isEmpty())) {
			IRecipe recipe;

			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItems(recipe);
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = Math.max((int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime()), 1);
				outputItemStacks = recipe.getOutput();
				outputFluidStacks = recipe.getFluidOutputs();

				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

				setMachineRunning(true); //turn on machine

			}
			else {
				setMachineRunning(false);
			}
		}
	}

	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return getTimeMultiplier();
	}

	protected float getPowerMultiplierForRecipe(IRecipe recipe) {
		return 1f;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		//Save output items if applicable
		if(outputItemStacks != null) {
			
			ListNBT list = new ListNBT();
			for(ItemStack stack : outputItemStacks) {
				if(stack != null) {
					CompoundNBT tag = new CompoundNBT();
					stack.write(tag);
					list.add(tag);
				}
			}
			nbt.put("outputItems", list);
		}

		if(outputFluidStacks != null) {
			ListNBT list = new ListNBT();
			for(FluidStack stack : outputFluidStacks) {
				if(stack != null) {
					CompoundNBT tag = new CompoundNBT();
					stack.writeToNBT(tag);
					list.add(tag);
				}
			}
			nbt.put("outputFluids", list);
		}
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		//Load output items being processed if applicable
		if(nbt.contains("outputItems")) {
			outputItemStacks = new LinkedList<ItemStack>();
			ListNBT list = nbt.getList("outputItems", 10);

			for(int i = 0; i < list.size(); i++) {
				CompoundNBT tag = list.getCompound(i);
				outputItemStacks.add(ItemStack.read(tag));
			}
		}

		//Load output fluids being processed if applicable
		if(nbt.contains("outputFluids")) {
			outputFluidStacks = new LinkedList<FluidStack>();
			ListNBT list = nbt.getList("outputFluids", 10);

			for(int i = 0; i < list.size(); i++) {
				CompoundNBT tag = list.getCompound(i);
				outputFluidStacks.add(FluidStack.loadFluidStackFromNBT(tag));
			}
		}
	}

	public boolean attemptCompleteStructure() {
		boolean completeStructure = super.attemptCompleteStructure(world.getBlockState(pos));
		if(completeStructure)
			onInventoryUpdated();

		return completeStructure;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);
	}
}
