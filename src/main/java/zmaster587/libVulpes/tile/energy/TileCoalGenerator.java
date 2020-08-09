package zmaster587.libVulpes.tile.energy;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

public class TileCoalGenerator extends TileInventoriedForgePowerMachine {

	int powerPerTick;
	private static final int INPUT_SLOT = 0;
	ModuleText textModule;

	public TileCoalGenerator() {
		super(LibVulpesTileEntityTypes.TILE_COAL_GENERATOR ,10000, 1);
		powerPerTick = 40;
		textModule = new ModuleText(40, 20, "Generating 0 RF/t", 0x2b2b2b);
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleSlotArray(40, 40, this, 0, 1));
		modules.add(new ModuleProgress(80, 40, 1, TextureResources.progressGenerator, this));
		modules.add(textModule);

		return modules;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		if(!canGeneratePower())
			setTimeAndAmounts();
	}

	@Override
	public int getPowerPerOperation() {
		return powerPerTick;
	}

	@Override
	protected void onOperationFinish() {
		super.onOperationFinish();
		setTimeAndAmounts();
	}

	private void setTimeAndAmounts() {
		
		timeRemaining = net.minecraftforge.common.ForgeHooks.getBurnTime(inventory.getStackInSlot(INPUT_SLOT));
		if(timeRemaining > 0)
			inventory.decrStackSize(INPUT_SLOT, 1);
	}

	@Override
	public void tick() {
		super.tick();
		if(world.isRemote)
			textModule.setText("Generating " + getLastAmtGenerated() + " RF/t");
	}
	@Override
	public String getModularInventoryName() {
		return "block.libvulpes.coal_generator";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public int getModularInvType() {
		return GuiHandler.guiId.MODULAR.ordinal();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int ID, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, ID, playerEntity, getModules(GuiHandler.guiId.MODULARNOINV.ordinal(), playerEntity), this);
	}
}