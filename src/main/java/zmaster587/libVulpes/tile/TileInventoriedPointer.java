package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public class TileInventoriedPointer extends TilePointer implements IInventoryMultiblock, ISidedInventory {

	public TileInventoriedPointer(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public int getSizeInventory() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getSizeInventory();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getStackInSlot(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).decrStackSize(i,j);
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			((IInventory)e).setInventorySlotContents(i,itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getInventoryStackLimit();
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity entityplayer) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isUsableByPlayer(entityplayer);
		return false;
	}

	@Override
	public boolean isEmpty() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isEmpty();
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			((IInventory)e).openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			((IInventory)e).closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isItemValidForSlot(i,itemstack);
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).getSlotsForFace(side);
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, Direction direction) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).canInsertItem(i,itemstack, direction);
		return true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, Direction direction) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).canExtractItem(i,itemstack, direction);

		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).removeStackFromSlot(index);

		return null;
	}

	@Override
	public void clear() {

	}
}