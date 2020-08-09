package zmaster587.libVulpes.tile.multiblock.hatch;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.EmbeddedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileInventoryHatch extends TilePointer implements ISidedInventory, IModularInventory, IInventoryUpdateCallback {

	protected EmbeddedInventory inventory;

	public TileInventoryHatch(TileEntityType<?> type) {
		super(type);
		inventory = new EmbeddedInventory(0, this);
	}

	public TileInventoryHatch(TileEntityType<?> type, int invSize) {
		super(type);
		inventory = new EmbeddedInventory(invSize, this);
	}
	
	public TileInventoryHatch() {
		super(LibVulpesTileEntityTypes.TILE_INPUT_HATCH);
		inventory = new EmbeddedInventory(0, this);
	}

	public TileInventoryHatch(int invSize) {
		super(LibVulpesTileEntityTypes.TILE_INPUT_HATCH);
		inventory = new EmbeddedInventory(invSize, this);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		inventory.write(nbt);
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		inventory.readFromNBT(nbt);
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> inventory).cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		setInventorySlotContentsNoUpdate(slot, stack);
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
	}


	public void setInventorySlotContentsNoUpdate(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 64;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void openInventory(PlayerEntity entity) {

	}

	@Override
	public void closeInventory(PlayerEntity entity) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return inventory.getSlotsForFace(side);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			Direction direction) {
		return inventory.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
		return inventory.canExtractItem(index, stack, direction);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleSlotArray(8, 18, this, 0, this.getSizeInventory()));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return null;
	}
	
	@Override
	public int getModularInvType() {
		return GuiHandler.guiId.MODULAR.ordinal();
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}
	
	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if(!world.isRemote) {
			TileEntity tile = getFinalPointedTile();
			if(tile instanceof TileMultiBlock) {
				((TileMultiBlock) tile).invalidateComponent(this);
			}
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public void onInventoryUpdated(int slot) {
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
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
