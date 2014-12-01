package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileInventoriedPointer extends TileEntityPointer implements IInventoryMultiblock, ISidedInventory {

	@Override
	public int getSizeInventory() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).getSizeInventory();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).getStackInSlot(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).decrStackSize(i,j);
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).getStackInSlotOnClosing(i);
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			 ((IInventory)e).setInventorySlotContents(i,itemstack);
	}

	@Override
	public String getInventoryName() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).getInventoryName();
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).hasCustomInventoryName();
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).getInventoryStackLimit();
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).isUseableByPlayer(entityplayer);
		return false;
	}

	@Override
	public void openInventory() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			((IInventory)e).openInventory();
	}

	@Override
	public void closeInventory() {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			((IInventory)e).closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof IInventory)
			return ((IInventory)e).isItemValidForSlot(i,itemstack);
		return false;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof ISidedInventory)
			return ((ISidedInventory)e).getAccessibleSlotsFromSide(var1);
		else if(e != null && e instanceof ISidedInventory) {
			int slots[] = new int[((IInventory)e).getSizeInventory()];
			
			for(int i = 0; i < slots.length; i++)
			{
				slots[i] = i;
			}
			return slots;
		}
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof ISidedInventory)
			return ((ISidedInventory)e).canInsertItem(i,itemstack, j);
		return true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		TileEntity e = worldObj.getTileEntity(masterX, masterY, masterZ);
		if(e != null && e instanceof ISidedInventory)
			return ((ISidedInventory)e).canExtractItem(i,itemstack, j);
		
		return true;
	}
}
