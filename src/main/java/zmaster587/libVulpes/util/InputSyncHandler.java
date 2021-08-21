package zmaster587.libVulpes.util;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;

import java.util.HashMap;

public class InputSyncHandler {

	public static HashMap<PlayerEntity, Boolean> spaceDown = new HashMap<PlayerEntity, Boolean>();
	

	public static boolean isSpaceDown(PlayerEntity player) {
		Boolean bool = spaceDown.get(player);
		
		return bool != null && bool;
	}
	
	//Called on server
	public static void updateKeyPress(PlayerEntity player, int key, boolean state) {
		ItemStack stack;
		switch(key) {
		case 0:
			
			stack = player.inventory.armorInventory.get(2);
			if(!stack.isEmpty()) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.setEnabledState(inv.getStackInSlot(i), !pack.isEnabled(inv.getStackInSlot(i)));
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
			
		case 1:
			stack = player.inventory.armorInventory.get(2);
			if(!stack.isEmpty()) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.changeMode(inv.getStackInSlot(i), inv, player);
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
		case GLFW.GLFW_KEY_SPACE : //SPACE
			spaceDown.put(player, state);
			break;
			
			default:
				
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
		spaceDown.remove(evt.getPlayer());
	}

	@SubscribeEvent
	public void onDimChanged(PlayerChangedDimensionEvent evt) {
		spaceDown.remove(evt.getPlayer());
	}
}
