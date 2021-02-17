package zmaster587.libVulpes.util;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class EmbeddedInventory extends ItemStackHandler implements ISidedInventory {

	ItemStackHandler handler;

	IInventoryUpdateCallback tile;

	NonNullList<Boolean> slotInsert;
	NonNullList<Boolean> slotExtract;

	public EmbeddedInventory(int size) {
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
		this.slotInsert = NonNullList.withSize(size, true);
		this.slotExtract = NonNullList.withSize(size, true);
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

		NBTTagList list2 = new NBTTagList();
		for(int i = 0; i < this.slotInsert.size(); i++) {
			NBTTagByte tag = new NBTTagByte((byte)((slotInsert.get(i) == true) ? 1 : 0));
				list2.appendTag(tag);
		}
		nbt.setTag("slotInsert", list2);

		NBTTagList list3 = new NBTTagList();
		for(int i = 0; i < this.slotExtract.size(); i++) {
			NBTTagByte tag = new NBTTagByte((byte)((slotExtract.get(i) == true) ? 1 : 0));
			list3.appendTag(tag);
		}
		nbt.setTag("slotExtract", list3);

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
		NBTTagList list2 = nbt.getTagList("slotInsert", 7);
		this.slotInsert = NonNullList.withSize(this.slotInsert.size(), false);
		for (int i = 0; i < list2.tagCount(); i++) {
			this.slotInsert.set(i, (list2.getIntAt(i) == 1) ? true : false);
		}
		NBTTagList list3 = nbt.getTagList("slotExtract", 7);
		this.slotInsert = NonNullList.withSize(this.slotExtract.size(), false);
		for (int i = 0; i < list3.tagCount(); i++) {
			this.slotExtract.set(i, (list3.getIntAt(i) == 1) ? true : false);
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

	public void setCanInsertSlot(int index, boolean bool) {
		this.slotInsert.set(index, bool);
	}

	public void setCanExtractSlot(int index, boolean bool) {
		this.slotExtract.set(index, bool);
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



	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.slotInsert.get(index);
	}


	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return this.slotExtract.get(index);
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
