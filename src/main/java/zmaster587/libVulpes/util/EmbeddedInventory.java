package zmaster587.libVulpes.util;

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

public class EmbeddedInventory implements  ISidedInventory {
	

		protected NonNullList<ItemStack> inv;
		
		ItemStackHandler handler;
		
		public EmbeddedInventory(int size) {
			inv = NonNullList.withSize(size, ItemStack.EMPTY);
			handler = new ItemStackHandler(inv);
		}
		
		public void writeToNBT(NBTTagCompound nbt) {

			nbt.setInteger("size", inv.size());

			NBTTagList list = new NBTTagList();
			for(int i = 0; i < inv.size(); i++)
			{
				ItemStack stack = inv.get(i);

				if(!stack.isEmpty()) {
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
			inv = NonNullList.withSize(Math.max(nbt.getInteger("size") == 0 ? 4 : nbt.getInteger("size"), inv.size()), ItemStack.EMPTY);
			handler = new ItemStackHandler(inv);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(i);
				byte slot = tag.getByte("Slot");
				if (slot >= 0 && slot < inv.size()) {
					inv.set(slot, new ItemStack(tag));
				}
			}
		}

		
		public int getSizeInventory() {
			return inv.size();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if(slot >= inv.size())
				return ItemStack.EMPTY;
			
			return inv.get(slot);
		}

		@Override
		public ItemStack decrStackSize(int slot, int amt) {
			ItemStack stack = inv.get(slot);
			if(!stack.isEmpty()) {
				ItemStack stack2 = stack.splitStack(Math.min(amt, stack.getCount()));
				if(stack.getCount() == 0)
					inv.set(slot, ItemStack.EMPTY);
				
				return stack2;
			}
			return null;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
            inv.set(slot, stack);
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
			return inv.get(slot).isEmpty() || (inv.get(slot).isItemEqual(item) && inv.get(slot).getMaxStackSize() != inv.get(slot).getCount());
		}

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			int array[] = new int[inv.size()];

			for(int i = 0; i < inv.size(); i++) {
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
			ItemStack stack = inv.get(index);
			inv.set(index, ItemStack.EMPTY);
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

		public IItemHandler getItemStackHandler()
		{
			return new ItemStackHandler(inv);
		}
}
