package zmaster587.libVulpes.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

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
	public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
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
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
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
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
