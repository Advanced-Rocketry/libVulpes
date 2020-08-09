package zmaster587.libVulpes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
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



	public boolean canInsertItem(int index, ItemStack itemStackIn,
			Direction direction) {
		return true;
	}


	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
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
	public void clear() {

	}
}
