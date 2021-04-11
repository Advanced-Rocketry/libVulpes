package zmaster587.libVulpes.util;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MultiInventory implements IInventory {

	List<IInventory> inventories;
	
	public MultiInventory(List<IInventory> list) {
		inventories = list;
	}
	
	@Override
	public int getSizeInventory() {
		int i = 0;
		for(IInventory inv : inventories) {
			i += inv.getSizeInventory();
		}
		return i;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.decrStackSize(i, j);
		}
		return null;
	}


	@Override
	public void setInventorySlotContents(int i, ItemStack j) {
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			inv.setInventorySlotContents(i,j);
			return;
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		for(IInventory inv : inventories ) {
			if(!inv.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack j) {
		
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.isItemValidForSlot(i,j);
			
		}
		return false;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		for(IInventory inv : inventories) {
			if(index >= inv.getSizeInventory()) {
				index -= inv.getSizeInventory();
				continue;
			}
			return inv.removeStackFromSlot(index);
			
		}
		return null;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
