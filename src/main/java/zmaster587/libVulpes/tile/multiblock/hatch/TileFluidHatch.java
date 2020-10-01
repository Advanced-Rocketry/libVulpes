package zmaster587.libVulpes.tile.multiblock.hatch;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.cap.FluidCapability;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.IconResource;

public class TileFluidHatch extends TilePointer implements IFluidHandlerInternal, IModularInventory, ISidedInventory {

	protected FluidTank fluidTank;
	private EmbeddedInventory inventory;
	private boolean outputOnly;
	
	public TileFluidHatch() {
		super(LibVulpesTileEntityTypes.TILE_FLUIDHATCH);
		fluidTank = new FluidTank(16000);
		inventory = new EmbeddedInventory(2);
	}
	
	public TileFluidHatch(int capacity) {
		super(LibVulpesTileEntityTypes.TILE_FLUIDHATCH);
		fluidTank = new FluidTank(capacity);
		inventory = new EmbeddedInventory(2);
	}
	
	public TileFluidHatch(TileEntityType<?> type) {
		super(type);
		fluidTank = new FluidTank(16000);
		inventory = new EmbeddedInventory(2);
	}
	
	public TileFluidHatch(TileEntityType<?> type, int capacity) {
		super(type);
		fluidTank = new FluidTank(capacity);
		inventory = new EmbeddedInventory(2);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> new FluidCapability(this)).cast();
		}
		else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> inventory).cast();
		}
		
		return super.getCapability(capability, facing);
	}

	public TileFluidHatch(boolean outputOnly) {
		this();

		this.outputOnly = outputOnly;
	}

	public boolean isOutputOnly() {
		return outputOnly;
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction doFill) {

		if(outputOnly)
			return 0;
		return fillInternal(resource, doFill);

	}

	
	@Override
	public FluidStack drain(FluidStack resource,
			FluidAction doDrain) {

		if(resource.isFluidEqual(fluidTank.getFluid())) {
			FluidStack fluidStack = fluidTank.drain(resource.getAmount(), doDrain);
			while(useBucket(0, getStackInSlot(0)));
			
			
			return fluidStack;
		}
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		return fluidTank.drain(maxDrain, doDrain);
	}
	@Override
	public FluidStack drainInternal(FluidStack maxDrain, FluidAction doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public int fillInternal(FluidStack resource, FluidAction doFill) {
		int fillAmt = fluidTank.fill(resource, doFill);
		while(useBucket(0, getStackInSlot(0)));
		
		if(doFill == FluidAction.EXECUTE && this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
		
		
		return fillAmt;
	}

	@Override
	public FluidStack drainInternal(int maxDrain, FluidAction doDrain) {
		return drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidStack getFluidInTank(int tank) {
		return fluidTank.getFluidInTank(tank);
	}
	
	@Override
	public int getTankCapacity(int tank) {
		return fluidTank.getTankCapacity(tank);
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> list = new ArrayList<ModuleBase>();

		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		if(world.isRemote)
			list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
		list.add(new ModuleLiquidIndicator(27, 18, this));

		return list;
	}
	
	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULAR;
	}

	@Override
	public String getModularInventoryName() {
		return outputOnly ? "block.libvulpes.fluidohatch" : "block.libvulpes.fluidihat";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
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
		inventory.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));
		
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		nbt.putBoolean("outputOnly", outputOnly);
		inventory.write(nbt);
		nbt.putInt("capacity", fluidTank.getCapacity());
		fluidTank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		outputOnly = nbt.getBoolean("outputOnly");
		inventory.readFromNBT(nbt);
		fluidTank = new FluidTank(nbt.getInt("capacity"));
		fluidTank.readFromNBT(nbt);
	}

	//Yes i was lazy
	//TODO: make better
	protected boolean useBucket( int slot, ItemStack stack) {
		return FluidUtils.attemptDrainContainerIInv(inventory, fluidTank, stack, 0, 1);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0,1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			Direction direction) {
		return index == 0 && isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
		return index == 1;
	}

	@Override
	public int getTanks() {
		return fluidTank.getTanks();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return fluidTank.isFluidValid(tank, stack);
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
