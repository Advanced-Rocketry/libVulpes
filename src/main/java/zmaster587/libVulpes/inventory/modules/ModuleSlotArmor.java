package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import javax.annotation.Nonnull;

import zmaster587.libVulpes.inventory.ContainerModular;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ModuleSlotArmor extends ModuleBase {

	int startSlot, endSlot;
	EntityPlayer player;
	ContainerModular container; //TODO: fix this OOPs (Object Oriented Poops)

	public ModuleSlotArmor(int offsetX, int offsetY, EntityPlayer player) {
		super(offsetX, offsetY);
		this.player = player;
		startSlot = 0;
		endSlot = 4;
	}

	public void setSlotBounds(int a, int b) {

		for(int i = startSlot; i < endSlot; i++) {
			container.inventorySlots.remove(i);
			container.inventoryItemStacks.remove(i);
		}
		startSlot = a;
		endSlot = b;
		
		for(Slot slot : getSlots(container)) {
			container.addSlotToContainer(slot);
		}
	}

	@Override
	public List<Slot> getSlots(Container container) {	

		//Get the reference to the container
		this.container = (ContainerModular)container;

		for(int i = 0; i + startSlot < endSlot; i++) {

			Slot slot;
			final int k = (endSlot - startSlot) - i - 1;
			
			PlayerArmorInvWrapper handler = new PlayerArmorInvWrapper(player.inventory);
			
			slot = new SlotItemHandler(handler, k + startSlot, offsetX + 18 * (i / 9), offsetY + 18*(i%9));


			slotList.add(slot);
		}
		return slotList;
	}
}

class PlayerCustomArmorInvWrapper  extends RangedWrapper
{
    private final InventoryPlayer inventoryPlayer;

    public PlayerCustomArmorInvWrapper(InventoryPlayer inv)
    {
        super(new InvWrapper(inv), inv.mainInventory.size(), 2*inv.mainInventory.size() + inv.armorInventory.size());
        inventoryPlayer = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        EntityEquipmentSlot equ = null;
        for (EntityEquipmentSlot s : EntityEquipmentSlot.values())
        {
            if (s.getSlotType() == EntityEquipmentSlot.Type.ARMOR && s.getIndex() + 36 == slot)
            {
                equ = s;
                break;
            }
        }
        // check if it's valid for the armor slot
        if (equ != null && slot < 40 && !stack.isEmpty() && stack.getItem().isValidArmor(stack, equ, getInventoryPlayer().player))
        {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public InventoryPlayer getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
