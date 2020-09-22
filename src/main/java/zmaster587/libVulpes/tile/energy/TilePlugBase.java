package zmaster587.libVulpes.tile.energy;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.UniversalBattery;

public abstract class TilePlugBase extends TilePointer implements IModularInventory, IUniversalEnergy, IMultiblock, IInventory {

	protected UniversalBattery storage;
	protected int teir;
	
	public TilePlugBase(TileEntityType<?> type) {
		super(type);
		storage = new UniversalBattery(getMaxEnergy(0));
	}
	
	public TilePlugBase(TileEntityType<?> type,int teir) {
		super(type);
		storage = new UniversalBattery(getMaxEnergy(0));
		setTeir(teir);
	}
	
	public void setTeir(int teir) {
		this.teir = teir;
		storage.setMaxEnergyStored(getMaxEnergy(teir));
	}
	
	protected int getMaxEnergy(int teir) {
		return (int)Math.pow(10,teir)*10000;
	}
	
	@Override
	public void setMaxEnergyStored(int max) {
		storage.setMaxEnergyStored(max);
		
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return LazyOptional.of(() -> (new ForgePowerCapability(this))).cast();
		
		return super.getCapability(capability, facing);
	}
	
	
	protected int getMaxDrainRate(int teir) {
		return 250*(int)Math.pow(2, teir);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putInt("teir", teir);
		storage.write(nbt);
		
		return nbt;
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);
		
		teir = nbt.getInt("teir");
		
		storage = new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}

	@Override
	public void setEnergyStored(int amt) {
		storage.setEnergyStored(amt);
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}
	
	
	@Override
	public void openInventory(PlayerEntity player) {
		
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void clear() {
		
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20,this));
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return storage.extractEnergy(amt, simulate);
	}

	@Override
	public int getUniversalEnergyStored() {
		return storage.getUniversalEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return  storage.acceptEnergy(amt, simulate);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULAR;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int ID, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, ID, playerEntity, getModules(getModularInvType().ordinal(), playerEntity), this, getModularInvType());
	}
}
