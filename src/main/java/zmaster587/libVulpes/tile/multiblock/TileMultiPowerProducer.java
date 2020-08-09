package zmaster587.libVulpes.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;

public class TileMultiPowerProducer extends TileMultiBlock implements IToggleButton, IModularInventory, INetworkMachine {

	protected MultiBattery batteries = new MultiBattery();
	protected boolean enabled;
	private ModuleToggleSwitch toggleSwitch;

	public TileMultiPowerProducer(TileEntityType<?> type) {
		super(type);
		enabled = false;
		toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	public boolean getMachineEnabled() {
		return enabled;
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if (id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
			toggleSwitch.setToggleState(getMachineEnabled());

			//Last ditch effort to update the toggle switch when it's flipped
			if(!world.isRemote)
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), world.func_230315_m_().func_241513_m_(), pos.getX(), pos.getY(), pos.getZ(), 64);
		}
	}
	
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.putBoolean("enabled", in.readBoolean());
		}
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {

		if(buttonId == 0) {
			this.setMachineEnabled(toggleSwitch.getState());
			PacketHandler.sendToServer(new PacketMachine(this,(byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()));
		}
	}
	
	public MultiBattery getBatteries() {
		return batteries;
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			setMachineEnabled(toggleSwitch.getState());
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

		return modules;
	}
	
	/**
	 * Handles distributing power production
	 */
	public void producePower(int amt) {
		batteries.acceptEnergy(amt, false);
	}
	
	@Override
	public String getModularInventoryName() {
		return getMachineName();
	}
	
	@Override
	public int getModularInvType() {
		return GuiHandler.guiId.MODULAR.ordinal();
	}
	
	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return isComplete();
	}
	
	public void resetCache() {
		batteries.clear();
		super.resetCache();
	}
	
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block : TileMultiBlock.getMapping('p')) {
			if(block.getBlock() == world.getBlockState(tile.getPos()).getBlock())
				batteries.addBattery((IUniversalEnergy) tile);
		}
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putBoolean("enabled", enabled);
	}
	
	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		enabled = nbt.getBoolean("enabled");
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int ID, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, ID, playerEntity, getModules(getModularInvType(), playerEntity), this);
	}
}
