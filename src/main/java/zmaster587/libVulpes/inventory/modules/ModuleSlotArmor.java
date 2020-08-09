package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import zmaster587.libVulpes.inventory.ContainerModular;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;

public class ModuleSlotArmor extends ModuleBase {

	int startSlot, endSlot;
	PlayerEntity player;
	ContainerModular container; //TODO: fix this OOPs (Object Oriented Poops)

	public ModuleSlotArmor(int offsetX, int offsetY, PlayerEntity player) {
		super(offsetX, offsetY);
		this.player = player;
		startSlot = 0;
		endSlot = 4;
	}

	public void setSlotBounds(int a, int b) {

		for(int i = startSlot; i < endSlot; i++) {
			container.inventorySlots.remove(i);
			container.getInventory().remove(i);
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
