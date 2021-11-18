package zmaster587.libVulpes.tile.multiblock;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.ITimeModifier;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.client.RepeatingSound;
import zmaster587.libVulpes.config.LibVulpesConfig;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;
import zmaster587.libVulpes.util.ZUtils;

public class TileMultiPowerConsumer extends TileMultiBlock implements INetworkMachine, IModularInventory, IProgressBar, IToggleButton, ITickableTileEntity, IToggleableMachine {

	protected MultiBattery batteries = new MultiBattery();

	private float timeMultiplier;
	protected int completionTime, currentTime;
	protected int powerPerTick;
	protected boolean enabled;
	protected ModuleToggleSwitch toggleSwitch;
	//On server determines change in power state, on client determines last power state on server
	boolean hadPowerLastTick;

	Object soundToPlay;

	public TileMultiPowerConsumer(TileEntityType<?> type) {
		super(type);
		enabled = false;
		completionTime = -1;
		currentTime = -1;
		hadPowerLastTick = true;
		timeMultiplier = 1;
		toggleSwitch = new ModuleToggleSwitch(160, 5, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	//Needed for GUI stuff
	public MultiBattery getBatteries() {
		return batteries;
	}

	public double getPowerMultiplier() {
		return LibVulpesConfig.common.powerMult.get();
	}

	
	@Override
	public int getProgress(int id) {
		return currentTime;
	}

	@Override
	public int getTotalProgress(int id) {
		return completionTime;
	}

	@Override
	public void setProgress(int id, int progress) {
		currentTime = progress;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		completionTime = progress;
	}

	@Override
	public float getNormallizedProgress(int id) {
		return completionTime > 0 ? currentTime/(float)completionTime : 0f;
	}

	public SoundEvent getSound() {
		return null;
	}

	public int getSoundDuration() {
		return 1;
	}

	/**
	 * 
	 * @param state
	 * @param tile can be null
	 * @return
	 */
	public float getTimeMultiplierForBlock(BlockState state, TileEntity tile) {
		
		Collection<ResourceLocation> blockTags = BlockTags.getCollection().getOwningTags(state.getBlock());

		if(state.getBlock() instanceof ITimeModifier)
			return ((ITimeModifier)state.getBlock()).getTimeMult();
		//Check coils, but with compat so people can add IE coils if wanted
		else if( blockTags.contains(new ResourceLocation("forge:coils/gold")) || blockTags.contains(new ResourceLocation("forge:coils/electrum")))
			return 0.9f;
		else if(blockTags.contains(new ResourceLocation("forge:coils/aluminum")) || blockTags.contains(new ResourceLocation("forge:coils/aluminium")) || blockTags.contains(new ResourceLocation("forge:coils/highvoltage")))
			return 0.8f;
		else if(blockTags.contains(new ResourceLocation("forge:coils/titanium")))
			return 0.75f;
		else if(blockTags.contains(new ResourceLocation("forge:coils/iridium")))
			return 0.5f;
		//Everything else is default
		return 1f;
	}

	public float getTimeMultiplier() {
		return timeMultiplier;
	}

	@Override
	protected void replaceStandardBlock(BlockPos newPos, BlockState state,
			TileEntity tile) {
		super.replaceStandardBlock(newPos, state, tile);
		timeMultiplier *= getTimeMultiplierForBlock(state, tile);
	}
	
	@Override
	public void tick() {

		//Freaky janky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0) {
			if(!world.isRemote) {
				if(isComplete())
					canRender = completeStructure = completeStructure(world.getBlockState(pos));
			}
			else {
				SoundEvent str;
				if((str = getSound()) != null) {
					playMachineSound(str);
				}
			}

			timeAlive = 0x1;
		}

		if(!world.isRemote && world.getGameTime() % 1000L == 0 && !isComplete()) {
			attemptCompleteStructure(world.getBlockState(pos));
			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}

		if(isRunning()) {
			if(hasEnergy(requiredPowerPerTick()) || (world.isRemote && hadPowerLastTick)) {

				onRunningPoweredTick();

				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!world.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
					}

					useEnergy(usedPowerPerTick());
				}
			}
			else if(!world.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
			}
		}
	}

	/**
	 * @return amount of power to allow the machine to run this tick
	 */
	protected int requiredPowerPerTick() {
		return  (int) Math.max(powerPerTick*getPowerMultiplier(),1);
	}

	/**
	 * @return the amount of power actually used by the machine this tick
	 */
	protected int usedPowerPerTick() {
		return requiredPowerPerTick();
	}

	protected void onRunningPoweredTick() {
		//Increment for both client and server
		currentTime++;

		SoundEvent str;
		if(world.isRemote && (str = getSound()) != null && world.getGameTime() % getSoundDuration() == 0) {
			//playMachineSound(str);
		}

		if(currentTime == completionTime)
			processComplete();
	}

	protected void playMachineSound(SoundEvent str) {
		//Screw you too

		if(soundToPlay == null && world.isRemote) {
			soundToPlay = new RepeatingSound(str, SoundCategory.BLOCKS, this);
		}

		LibVulpes.proxy.playSound(soundToPlay);
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
		if(!world.isRemote) {
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}

	}

	public boolean getMachineEnabled() {
		return enabled;
	}

	public void resetCache() {
		batteries.clear();
		super.resetCache();
	}

	/**
	 * @param world world
	 * @param destroyedPos coords of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, BlockState state) {
		resetCache();
		completionTime = 0;
		currentTime = 0;
		enabled = false;
		timeMultiplier = 1f;

		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;

		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	/**
	 * True if the machine is running
	 * @return true if the machine is currently processing something, or more formally, if completionTime > 0
	 */
	public boolean isRunning() {
		return completionTime > 0 && isComplete();
	}

	public void useEnergy(int amt) {
		batteries.extractEnergy(amt, false);
	}

	public boolean hasEnergy(int amt) {
		return batteries.getUniversalEnergyStored() >= amt;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block : TileMultiBlock.getMapping('P')) {
			if(block.getBlock() == world.getBlockState(tile.getPos()).getBlock())
				batteries.addBattery((IUniversalEnergy) tile);
		}
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putInt("completionTime", this.completionTime);
		nbt.putInt("currentTime", this.currentTime);
		nbt.putInt("powerPerTick", this.powerPerTick);
		nbt.putBoolean("enabled", enabled);

		if(timeMultiplier != 1)
			nbt.putFloat("timeMult", timeMultiplier);
	}

	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		completionTime = nbt.getInt("completionTime");
		currentTime = nbt.getInt("currentTime");
		powerPerTick = nbt.getInt("powerPerTick");
		enabled = nbt.getBoolean("enabled");

		if(nbt.contains("timeMult"))
			timeMultiplier = nbt.getFloat("timeMult");

		if(world != null && world.isRemote && isRunning()) {
			((BlockTile)getBlockState().getBlock()).setBlockState(world,world.getBlockState(getPos()), getPos(),true);
		}
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			out.writeBoolean(hadPowerLastTick);
		}
		else if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == NetworkPackets.POWERERROR.ordinal()) {
			nbt.putBoolean("hadPowerLastTick", in.readBoolean());
		}
		else if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.putBoolean("enabled", in.readBoolean());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		} else if (id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
			toggleSwitch.setToggleState(getMachineEnabled());
			//Last ditch effort to update the toggle switch when it's flipped
			if(!world.isRemote)
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), ZUtils.getDimensionId(world) /* get dimension */, pos.getX(), pos.getY(), pos.getZ(), 64);
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		LinkedList<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModulePower(18, 20, getBatteries()));

		modules.add(toggleSwitch);
		modules.add(new ModuleText(140, 40, String.format("Speed:\n%.2fx", 1/getTimeMultiplier()), 0x2d2d2d));
		modules.add(new ModuleText(140, 60, String.format("Power:\n%.2fx", 1f), 0x2d2d2d));
		
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return getMachineName();
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		Block block = getBlockState().getBlock();
		return block instanceof BlockTile ? ((BlockTile)block).getGuid() : GuiHandler.guiId.MODULAR;
	}
	
	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == toggleSwitch) {
			this.setMachineEnabled(toggleSwitch.getState());
			PacketHandler.sendToServer(new PacketMachine(this,(byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()));
		}
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			setMachineEnabled(toggleSwitch.getState());
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return isComplete();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int ID, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, ID, playerEntity, getModules(getModularInvType().ordinal(), playerEntity), this,getModularInvType());
	}
}
