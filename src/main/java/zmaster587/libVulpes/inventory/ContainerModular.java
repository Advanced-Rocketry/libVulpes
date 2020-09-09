package zmaster587.libVulpes.inventory;

import java.util.List;

import javax.annotation.Nullable;

import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ContainerModular extends Container {

	List<ModuleBase> modules;
	int numSlots, numTrackers;
	IModularInventory modularInventory;
	public boolean includePlayerInv;

	public static ContainerModular createFromNetworkItem(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		ItemStack stack = GuiHandler.getHeldFromBuf(buf);
		IModularInventory modularItem = (IModularInventory)stack.getItem();
		int ID = modularItem.getModularInvType();
		
		boolean includePlayerInv = GuiHandler.doesIncludePlayerInv(ID);
		boolean includeHotBar = GuiHandler.doesIncludeHotBar(ID);
		
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_HELD_ITEM, windowId, invPlayer.player, modularItem.getModules(ID, invPlayer.player), modularItem, includePlayerInv, includeHotBar);
	}
	
	public static ContainerModular createFromNetworkBlock(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		TileEntity tile = GuiHandler.getTeFromBuf(buf);
		IModularInventory modularItem = (IModularInventory)tile;
		int ID = modularItem.getModularInvType();
		
		boolean includePlayerInv = GuiHandler.doesIncludePlayerInv(ID);
		boolean includeHotBar = GuiHandler.doesIncludeHotBar(ID);
		
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, windowId, invPlayer.player, modularItem.getModules(ID, invPlayer.player), modularItem, includePlayerInv, includeHotBar);
	}
	
	public static ContainerModular createFromEntity(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		Entity tile = GuiHandler.getEntityFromBuf(buf);
		IModularInventory modularItem = (IModularInventory)tile;
		int ID = modularItem.getModularInvType();
		
		boolean includePlayerInv = GuiHandler.doesIncludePlayerInv(ID);
		boolean includeHotBar = GuiHandler.doesIncludeHotBar(ID);
		
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, windowId, invPlayer.player, modularItem.getModules(ID, invPlayer.player), modularItem, includePlayerInv, includeHotBar);
	
	}
	
	public ContainerModular(@Nullable ContainerType<?> type, int id, PlayerEntity playerInv, List<ModuleBase> modules, IModularInventory modulularInv)
	{
		this(type, id, playerInv, modules, modulularInv, GuiHandler.doesIncludePlayerInv(modulularInv.getModularInvType()), GuiHandler.doesIncludeHotBar(modulularInv.getModularInvType()));
	}
	
	public ContainerModular(@Nullable ContainerType<?> type, int id, PlayerEntity playerInv, List<ModuleBase> modules, IModularInventory modulularInv, boolean includePlayerInv, boolean includeHotBar) {
		super(type,  id);
		this.modularInventory = modulularInv;
		this.modules = modules;
		numSlots = 0;
		numTrackers = 0;
		this.includePlayerInv = includePlayerInv;

		for(ModuleBase module : modules)
		{
			for(Slot slot : module.getSlots(this)) {
				addSlotToContainer(slot);
				numSlots++;
			}
			
			for(IntReferenceHolder slot : module.getIntTrackers(this)) {
				trackInt(slot);
				numTrackers++;
			}
			
		}

		if(includePlayerInv) {
			// Player inventory
			for (int i1 = 0; i1 < 3; i1++) {
				for (int l1 = 0; l1 < 9; l1++) {
					addSlotToContainer(new Slot(playerInv.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
				}
			}
		}

		if(includeHotBar) {
			// Player hotbar
			for (int j1 = 0; j1 < 9; j1++) {
				addSlotToContainer(new Slot(playerInv.inventory, j1, 8 + j1 * 18, 147));
			}
		}
	}

	public Slot addSlotToContainer(Slot slot) {
		return super.addSlot(slot);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			//for(int i = 0; i < module.numChangesToSend(); i++) {
			module.sendInitialChanges(this, listener, moduleIndex);

			moduleIndex+= module.numberOfChangesToSend();
			//}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			for(int i = 0; i < module.numberOfChangesToSend(); i++) {
				if(module.isUpdateRequired(i)) {

					List<IContainerListener> listeners = ObfuscationReflectionHelper.getPrivateValue(Container.class, this, "listeners");
					for (int j = 0; j < listeners.size(); ++j) {
						module.sendChanges(this, ((IContainerListener)listeners.get(j)), moduleIndex, i);
					}
				}
				moduleIndex++;
			}
		}
	}

	@Override
	public void updateProgressBar(int slot, int value) {
		//super.updateProgressBar(slot, value);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			if(slot - moduleIndex < module.numberOfChangesToSend() && slot - moduleIndex >= 0) {
				module.onChangeRecieved(slot - moduleIndex, value);
			}
			moduleIndex += module.numberOfChangesToSend();
		}

	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotId) {

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			//Try to merge to player inventory
			if (slotId < numSlots)
			{
				if (!this.mergeItemStack(itemstack1, numSlots, this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, slotId, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0)
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
	
	 /**
     * merges provided ItemStack with the first avaliable one in the container/player inventory
     */
    /*protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
    {
        boolean flag1 = false;
        int k = p_75135_2_;

        if (p_75135_4_)
        {
            k = p_75135_3_ - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (p_75135_1_.isStackable())
        {
            while (p_75135_1_.getCount() > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 != ItemStack.EMPTY && itemstack1.getItem() == p_75135_1_.getItem() && (!p_75135_1_.getHasSubtypes() || p_75135_1_.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(p_75135_1_, itemstack1))
                {
                    int l = itemstack1.getCount() + p_75135_1_.getCount();

                    if (l <= p_75135_1_.getCount())
                    {
                        p_75135_1_.setCount(0);
                        itemstack1.setCount(l);
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.getCount() < p_75135_1_.getCount())
                    {
                        p_75135_1_.setCount(p_75135_1_.getCount() - p_75135_1_.getCount() - itemstack1.getCount());
                        itemstack1.setCount(p_75135_1_.getCount());
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (p_75135_1_.getCount() > 0)
        {
            if (p_75135_4_)
            {
                k = p_75135_3_ - 1;
            }
            else
            {
                k = p_75135_2_;
            }

            while (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_)
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                //For some awful reason MC doesn't seem to check if a stack is valid...
                if (itemstack1 == ItemStack.EMPTY && slot.isItemValid(p_75135_1_))
                {
                    slot.putStack(p_75135_1_.copy());
                    slot.onSlotChanged();
                    p_75135_1_.setCount(0);
                    flag1 = true;
                    break;
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        return flag1;
    }*/

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return modularInventory.canInteractWithContainer(player);
	}
}
