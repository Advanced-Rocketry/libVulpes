package zmaster587.libVulpes.inventory;

import java.util.List;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerModular extends Container {

	List<ModuleBase> modules;
	int numSlots;
	IModularInventory modularInventory;


	public ContainerModular(EntityPlayer playerInv, List<ModuleBase> modules, IModularInventory modulularInv, boolean includePlayerInv, boolean includeHotBar) {
		this.modularInventory = modulularInv;
		this.modules = modules;
		numSlots = 0;

		for(ModuleBase module : modules)
			for(Slot slot : module.getSlots(this)) {
				addSlotToContainer(slot);
				numSlots++;
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
		return super.addSlotToContainer(slot);
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

					for (int j = 0; j < this.listeners.size(); ++j) {
						module.sendChanges(this, ((IContainerListener)this.listeners.get(j)), moduleIndex, i);
					}
				}
				moduleIndex++;
			}
		}
	}

	@Override
	public void updateProgressBar(int slot, int value) {
		super.updateProgressBar(slot, value);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			if(slot - moduleIndex < module.numberOfChangesToSend() && slot - moduleIndex >= 0) {
				module.onChangeRecieved(slot - moduleIndex, value);
			}
			moduleIndex += module.numberOfChangesToSend();
		}

	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {

		ItemStack itemstack = null;
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
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, slotId, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
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
    protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
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
            while (p_75135_1_.stackSize > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 != null && itemstack1.getItem() == p_75135_1_.getItem() && (!p_75135_1_.getHasSubtypes() || p_75135_1_.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(p_75135_1_, itemstack1))
                {
                    int l = itemstack1.stackSize + p_75135_1_.stackSize;

                    if (l <= p_75135_1_.getMaxStackSize())
                    {
                        p_75135_1_.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < p_75135_1_.getMaxStackSize())
                    {
                        p_75135_1_.stackSize -= p_75135_1_.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = p_75135_1_.getMaxStackSize();
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

        if (p_75135_1_.stackSize > 0)
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
                if (itemstack1 == null && slot.isItemValid(p_75135_1_))
                {
                    slot.putStack(p_75135_1_.copy());
                    slot.onSlotChanged();
                    p_75135_1_.stackSize = 0;
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
    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return modularInventory.canInteractWithContainer(player);
	}
}
