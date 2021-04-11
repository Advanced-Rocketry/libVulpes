package zmaster587.libVulpes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;

import javax.annotation.Nonnull;
import java.util.ArrayList;

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

	public void write(CompoundNBT nbt) {

		nbt.putInt("size", this.stacks.size());

		ListNBT list = new ListNBT();
		for(int i = 0; i < this.stacks.size(); i++)
		{
			ItemStack stack = this.stacks.get(i);

			if(!stack.isEmpty()) {
				CompoundNBT tag = new CompoundNBT();
				tag.putByte("Slot", (byte)(i));
				stack.write(tag);
				list.add(tag);
			}
		}

		nbt.put("outputItems", list);

		ArrayList list2 = new ArrayList<Byte>();
		for(int i = 0; i < this.slotInsert.size(); i++) {
			list2.add(i, slotInsert.get(i) ? (byte)1 : (byte)0);
		}
		nbt.put("slotInsert", new ByteArrayNBT(list2));

		ArrayList list3 = new ArrayList<Byte>();
		for(int i = 0; i < this.slotExtract.size(); i++) {
			list3.add(i, slotExtract.get(i) ? (byte)1 : (byte)0);
		}
		nbt.put("slotExtract", new ByteArrayNBT(list3));

	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		if(tile != null)
			tile.onInventoryUpdated(slot);

	}

	public void readFromNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("outputItems", 10);
		this.stacks = NonNullList.withSize(Math.max(nbt.getInt("size") == 0 ? 4 : nbt.getInt("size"), this.stacks.size()), ItemStack.EMPTY);
		handler = new ItemStackHandler(this.stacks);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT tag = (CompoundNBT) list.getCompound(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.stacks.size()) {
				this.stacks.set(slot, ItemStack.read(tag));
			}
		}


		byte[] list2 = nbt.getByteArray("slotInsert");
		this.slotInsert = NonNullList.withSize(list2.length, false);
		for (int i = 0; i < list2.length; i++) {
			this.slotInsert.set(i, (list2[i] == 1) ? true : false);
		}
		byte[] list3 = nbt.getByteArray("slotExtract");
		this.slotExtract = NonNullList.withSize(list3.length, false);
		for (int i = 0; i < list3.length; i++) {
			this.slotExtract.set(i, (list3[i] == 1) ? true : false);
		}

		//Backcompat, to allow older worlds to load
        if (this.slotInsert.size() == 0) {
			this.slotInsert = NonNullList.withSize(4, false);
		}
		if (this.slotExtract.size() == 0) {
			this.slotExtract = NonNullList.withSize(4, false);
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
			ItemStack stack2 = stack.split(Math.min(amt, stack.getCount()));
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
	public boolean isUsableByPlayer(PlayerEntity player) {
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
	public void openInventory(PlayerEntity player) {

	}

	@Override
	public void closeInventory(PlayerEntity player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return this.stacks.get(slot).isEmpty() || (this.stacks.get(slot).isItemEqual(item) && this.stacks.get(slot).getMaxStackSize() != this.stacks.get(slot).getCount());
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		int array[] = new int[this.stacks.size()];

		for(int i = 0; i < this.stacks.size(); i++) {
			array[i] = i;
		}
		return array;
	}


	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return this.slotInsert.get(index);
	}


	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return this.slotExtract.get(index);
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (!slotInsert.isEmpty() && slotInsert.get(slot) == true ){
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (!slotExtract.isEmpty() && slotExtract.get(slot) == true ){
			return super.extractItem(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
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
	public void clear() {

	}
}
