package zmaster587.libVulpes.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

public class EmbeddedInventory implements ISidedInventory {

		protected ItemStack inv[];
		
		public EmbeddedInventory(int size) {
			inv = new ItemStack[size];
		}
		
		public void writeToNBT(NBTTagCompound nbt) {

			nbt.setInteger("size", inv.length);

			NBTTagList list = new NBTTagList();
			for(int i = 0; i < inv.length; i++)
			{
				ItemStack stack = inv[i];

				if(stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte)(i));
					stack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			
			nbt.setTag("outputItems", list);
		}

		public void readFromNBT(NBTTagCompound nbt) {
			NBTTagList list = nbt.getTagList("outputItems", 10);
			inv = new ItemStack[Math.max(nbt.getInteger("size") == 0 ? 4 : nbt.getInteger("size"), inv.length)];

			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(i);
				byte slot = tag.getByte("Slot");
				if (slot >= 0 && slot < inv.length) {
					inv[slot] = new ItemStack(tag);
				}
			}
		}

		@Override
		public int getSizeInventory() {
			return inv.length;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if(slot >= inv.length)
				return null;
			
			return inv[slot];
		}

		@Override
		public ItemStack decrStackSize(int slot, int amt) {
			ItemStack stack = inv[slot];
			if(stack != null) {
				ItemStack stack2 = stack.splitStack(Math.min(amt, stack.getCount()));
				if(stack.getCount() == 0)
					inv[slot] = null;
				
				return stack2;
			}
			return null;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			inv[slot] = stack;
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
			for(ItemStack i : inv) {
				if(i != null)
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
			return inv[slot] == null || (inv[slot].isItemEqual(item) && inv[slot].getMaxStackSize() != inv[slot].getCount());
		}

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			int array[] = new int[inv.length];

			for(int i = 0; i < inv.length; i++) {
				array[i] = i;
			}
			return array;
		}

		
		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return true;
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return true;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public void markDirty() {
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			ItemStack stack = inv[index];
			inv[index] = null;
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
			return null;
		}
}
