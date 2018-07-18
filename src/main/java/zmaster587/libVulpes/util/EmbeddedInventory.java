package zmaster587.libVulpes.util;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;

public class EmbeddedInventory extends ItemStackHandler implements ISidedInventory {

	ItemStackHandler handler;

	IInventoryUpdateCallback tile;

	public EmbeddedInventory(int size) {
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public EmbeddedInventory(int size, IInventoryUpdateCallback tile) {
		this(size);
		this.tile = tile;
	}

	public void writeToNBT(NBTTagCompound nbt) {

		nbt.setInteger("size", this.stacks.size());

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < this.stacks.size(); i++)
		{
			ItemStack stack = this.stacks.get(i);

			if(!stack.isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
		}

		nbt.setTag("outputItems", list);
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		if(tile != null)
			tile.onInventoryUpdated(slot);

	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("outputItems", 10);
		this.stacks = NonNullList.withSize(Math.max(nbt.getInteger("size") == 0 ? 4 : nbt.getInteger("size"), this.stacks.size()), ItemStack.EMPTY);
		handler = new ItemStackHandler(this.stacks);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.stacks.size()) {
				this.stacks.set(slot, new ItemStack(tag));
			}
		}
	}


	public int getSizeInventory() {
		return this.stacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot >= this.stacks.size())
			return ItemStack.EMPTY;

		return this.stacks.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = this.stacks.get(slot);
		if(!stack.isEmpty()) {
			ItemStack stack2 = stack.splitStack(Math.min(amt, stack.getCount()));
			if(stack.getCount() == 0)
				this.stacks.set(slot, ItemStack.EMPTY);

			return stack2;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack i : this.stacks) {
			if(i != null && !i.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return this.stacks.get(slot).isEmpty() || (this.stacks.get(slot).isItemEqual(item) && this.stacks.get(slot).getMaxStackSize() != this.stacks.get(slot).getCount());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int array[] = new int[this.stacks.size()];

		for(int i = 0; i < this.stacks.size(); i++) {
			array[i] = i;
		}
		return array;
	}



	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return true;
	}


	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return true;
	}


	public String getName() {
		return "";
	}

	@Override
	public void markDirty() {
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.stacks.get(index);
		this.stacks.set(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Inventory");
	}
}
