package zmaster587.libVulpes.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.ITimeModifier;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.client.RepeatingSound;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;

public class TileMultiPowerConsumer extends TileMultiBlock implements INetworkMachine, IModularInventory, IProgressBar, IToggleButton, IToggleableMachine {

	protected MultiBattery batteries = new MultiBattery();

	private float timeMultiplier;
	protected int completionTime, currentTime;
	protected int powerPerTick;
	protected boolean enabled;
	protected ModuleToggleSwitch toggleSwitch;
	//On server determines change in power state, on client determines last power state on server
	boolean hadPowerLastTick = true;

	Object sound;

	public TileMultiPowerConsumer() {
		super();
		enabled = false;
		completionTime = -1;
		currentTime = -1;
		hadPowerLastTick = true;
		timeMultiplier = 1;
		toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	//Needed for GUI stuff
	public MultiBattery getBatteries() {
		return batteries;
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

	@Override
	public boolean canUpdate() {
		return true;
	}


	public ResourceLocation getSound() {
		return null;
	}

	public int getSoundDuration() {
		return 1;
	}

	/**
	 * 
	 * @param block
	 * @param tile can be null
	 * @return
	 */
	public float getTimeMultiplierForBlock(Block block, int meta, TileEntity tile) {
		if(block instanceof ITimeModifier)
			return ((ITimeModifier)block).getTimeMult();
		return 1f;
	}

	public float getTimeMultiplier() {
		return timeMultiplier;
	}

	@Override
	protected void replaceStandardBlock(int x, int y, int z, Block block, int meta,
			TileEntity tile) {
		super.replaceStandardBlock(x, y, z, block, meta, tile);
		timeMultiplier *= getTimeMultiplierForBlock(block, meta, tile);
	}


	@Override
	public void updateEntity() {
		super.updateEntity();

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0) {
			if(!worldObj.isRemote) {
				if(isComplete())
					canRender = completeStructure = completeStructure();
				onCreated();
			}
			else {
				ResourceLocation str = getSound();
				if(str != null)
					playMachineSound(getSound());
			}

			timeAlive = 0x1;
		}

		if(!worldObj.isRemote && worldObj.getTotalWorldTime() % 1000L == 0 && !isComplete()) {
			attemptCompleteStructure();
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		if(isRunning()) {
			if(hasEnergy(requiredPowerPerTick()) || (worldObj.isRemote && hadPowerLastTick)) {

				onRunningPoweredTick();

				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!worldObj.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}

					useEnergy(usedPowerPerTick());
				}
			}
			else if(!worldObj.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	protected void onCreated() {};

	/**
	 * @return amount of power to allow the machine to run this tick
	 */
	protected int requiredPowerPerTick() {
		return powerPerTick;
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

		if(currentTime == completionTime)
			processComplete();
	}

	protected void playMachineSound(ResourceLocation str) {
		LibVulpes.proxy.playSound(new RepeatingSound(str, this));
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
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
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, int destroyedX, int destroyedY, int destroyedZ, boolean blockBroken) {
		resetCache();
		completionTime = 0;
		currentTime = 0;
		enabled = false;
		timeMultiplier = 1f;

		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ, blockBroken);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		return batteries.getEnergyStored() >= amt;
	}

	public void setMachineRunning(boolean running) {
		if(running && this.getBlockMetadata() < 8) {
			this.blockMetadata = getBlockMetadata() | 8;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2);
		}
		else if(!running && this.blockMetadata >= 8) {
			this.blockMetadata = getBlockMetadata() & 7;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2); //Turn off machine
		}
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block : TileMultiBlock.getMapping('P')) {
			if(block.getBlock() == worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord))
				batteries.addBattery((IUniversalEnergy) tile);
		}
	}

	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setInteger("completionTime", this.completionTime);
		nbt.setInteger("currentTime", this.currentTime);
		nbt.setInteger("powerPerTick", this.powerPerTick);
		nbt.setBoolean("enabled", enabled);
		if(timeMultiplier != 1)
			nbt.setFloat("timeMult", timeMultiplier);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		completionTime = nbt.getInteger("completionTime");
		currentTime = nbt.getInteger("currentTime");
		powerPerTick = nbt.getInteger("powerPerTick");
		enabled = nbt.getBoolean("enabled");

		if(nbt.hasKey("timeMult"))
			timeMultiplier = nbt.getFloat("timeMult");
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			out.writeBoolean(hadPowerLastTick);
		}
		else if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == NetworkPackets.POWERERROR.ordinal()) {
			nbt.setBoolean("hadPowerLastTick", in.readBoolean());
		}
		else if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.setBoolean("enabled", in.readBoolean());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		} else if (id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
			toggleSwitch.setToggleState(getMachineEnabled());

			//Last ditch effort to update the toggle switch when it's flipped
			if(!worldObj.isRemote)
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

		modules.add(new ModuleText(140, 40, String.format("Speed:\n%.2fx", 1/getTimeMultiplier()), 0x2d2d2d));
		modules.add(new ModuleText(140, 60, String.format("Power:\n%.2fx", 1f), 0x2d2d2d));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return getMachineName();
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 0) {
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
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return isComplete();
	}
}
