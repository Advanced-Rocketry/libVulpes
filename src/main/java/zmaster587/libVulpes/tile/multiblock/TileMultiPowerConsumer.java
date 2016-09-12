package zmaster587.libVulpes.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;

public class TileMultiPowerConsumer extends TileMultiBlock implements INetworkMachine, IModularInventory, IProgressBar, IToggleButton, ITickable {

	protected MultiBattery batteries = new MultiBattery();

	protected int completionTime, currentTime;
	protected int powerPerTick;
	protected boolean enabled;
	private ModuleToggleSwitch toggleSwitch;
	//On server determines change in power state, on client determines last power state on server
	boolean hadPowerLastTick = true;

	public TileMultiPowerConsumer() {
		super();
		enabled = false;
		completionTime = -1;
		currentTime = -1;
		hadPowerLastTick = true;
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
	
	public String getSound() {
		return null;
	}
	
	public int getSoundDuration() {
		return 1;
	}
	
	@Override
	public void update() {

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 && !worldObj.isRemote) {
			if(isComplete())
				canRender = completeStructure = completeStructure();
			timeAlive = 0x1;
		}
		
		if(!worldObj.isRemote && worldObj.getTotalWorldTime() % 1000L == 0 && !isComplete()) {
			attemptCompleteStructure();
			markDirty();
		}
		
		if(isRunning()) {
			if(hasEnergy(powerPerTick) || (worldObj.isRemote && hadPowerLastTick)) {

				onRunningPoweredTick();

				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!worldObj.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
					}

					useEnergy(powerPerTick);
				}
			}
			else if(!worldObj.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
			}
		}
	}

	protected void onRunningPoweredTick() {
		//Increment for both client and server
		currentTime++;

		String str;
		if(worldObj.isRemote && (str = getSound()) != null && worldObj.getTotalWorldTime() % getSoundDuration() == 0) {
			playMachineSound(str);
		}
		
		if(currentTime == completionTime)
			processComplete();
	}
	
	protected void playMachineSound(String str) {
		//Screw you too
		//worldObj.playSound(Minecraft.getMinecraft().thePlayer, pos, str, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS),  0.975f + worldObj.rand.nextFloat()*0.05f);
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
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken) {
		resetCache();
		completionTime = 0;
		currentTime = 0;
		enabled = false;

		super.deconstructMultiBlock(world, destroyedPos, blockBroken);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;

		this.markDirty();
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

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block : TileMultiBlock.getMapping('P')) {
			if(block.getBlock() == worldObj.getBlockState(tile.getPos()))
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
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		completionTime = nbt.getInteger("completionTime");
		currentTime = nbt.getInteger("currentTime");
		powerPerTick = nbt.getInteger("powerPerTick");
		enabled = nbt.getBoolean("enabled");
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
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64);
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

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
