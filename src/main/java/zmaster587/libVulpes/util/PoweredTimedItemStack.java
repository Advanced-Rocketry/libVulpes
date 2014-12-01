package zmaster587.libVulpes.util;

import net.minecraft.item.ItemStack;

public class PoweredTimedItemStack {
	ItemStack storedItem;
	int storedPower, storedTime;
	public PoweredTimedItemStack(ItemStack item, int power, int time) {
		storedItem = item;
		storedPower = power;
		storedTime = time;
	}
	
	public ItemStack getItemStack() { return storedItem; }
	public int getTime() { return storedTime;}
	public int getPower() { return storedPower; }
}
