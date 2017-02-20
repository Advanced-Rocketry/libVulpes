package zmaster587.libVulpes.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import zmaster587.libVulpes.util.ZUtils.RedstoneState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class ZUtils {

	public enum RedstoneState {
		ON,
		OFF,
		INVERTED;

		public RedstoneState getNext() {
			int  i = ordinal()+1;
			if(i >= RedstoneState.values().length)
				return RedstoneState.values()[0];
			else
				return RedstoneState.values()[i];
		}

		public RedstoneState getPrev() {
			int  i = ordinal()-1;
			if(i < 0)
				return RedstoneState.values()[RedstoneState.values().length-1];
			else
				return RedstoneState.values()[i];
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setByte("redstoneState", (byte)this.ordinal());
		}
		
		public static RedstoneState createFromNBT(NBTTagCompound tag) {
			return RedstoneState.values()[tag.getByte("redstoneState")];
		}
	}

	public static int getAverageColor(long r, long g, long b, int total) {
		return (int)(( (r/total) ) | ( (g/total) << 8 ) | ( ( b/total ) << 16 ) );
	}

	public static int getDirectionFacing(float rotationYaw) {
		int l = MathHelper.floor_double((double)(MathHelper.wrapAngleTo180_float(rotationYaw) * 4.0F / 360.0F) + 0.5D) & 3;

		if(l == 0)
			l = 2;
		else if(l == 1)
			l = 5;
		else if(l == 2)
			l = 3;
		else
			l = 4;

		return l;
	}

	public static List copyRandomElements(List fromList, int maxElements) {

		List returnList = new LinkedList();
		List moveList = new LinkedList(fromList);
		Random rand = new Random(System.nanoTime());

		for(int i = 0; i < maxElements && !moveList.isEmpty(); i++) {
			returnList.add(moveList.remove(rand.nextInt(moveList.size())));
		}
		return returnList;
	}

	/**
	 * @param axis Axis Aligned Bounding box to rotate
	 * @param angleDeg amount to rotate the bounding box in radians
	 * @return copy of axis with the rotation applied
	 */
	public static AxisAlignedBB rotateAABB(AxisAlignedBB axis, double angleDeg) {

		double minX, maxX, minZ, maxZ;
		double angle = Math.toRadians(90 - angleDeg);

		minZ = -((axis.minX*Math.sin(angle)) + (axis.minZ*Math.cos(angle)));
		maxZ = -((axis.maxX*Math.sin(angle)) + (axis.maxZ*Math.cos(angle)));

		minX = -((axis.minZ*Math.sin(angle)) + (axis.minX*Math.cos(angle)));
		maxX = -((axis.maxZ*Math.sin(angle)) + (axis.maxX*Math.cos(angle)));

		if(minZ > maxZ) {
			double buffer = minZ;
			minZ = maxZ;
			maxZ = buffer;
		}
		if(minX > maxZ) {
			double buffer = minX;
			minX = maxX;
			maxX = buffer;
		}

		AxisAlignedBB ret = axis.copy().setBounds(minX,
				axis.minY,
				minZ,
				maxX,
				axis.maxY,
				maxZ);

		return ret;
	}

	/**
	 * 
	 * @param local bounding box in local coords
	 * @param global global bounding box
	 * @param e entity that owns the bounding box
	 * @param angle amount to rotate by
	 * @return rotated bounding box in global coords
	 */
	public static AxisAlignedBB convertLocalBBToGlobal(AxisAlignedBB local, AxisAlignedBB global, Entity e, double angle) {
		AxisAlignedBB rotatedLocal = rotateAABB(local, angle);

		return AxisAlignedBB.getBoundingBox(e.posX + rotatedLocal.minX, e.posY + rotatedLocal.minY, e.posZ + rotatedLocal.minZ, rotatedLocal.maxX + e.posX, rotatedLocal.maxY + e.posY, rotatedLocal.maxZ + e.posZ);
	}

	public static String formatNumber(int number) {
		if(number > 999999999) 
			return ((number/1000000)/10f) + "T";
		if(number > 999999)
			return ((number/1000000)/10f) + "M";
		if(number > 999) 
			return ((number/100)/10f) + "K";

		return String.valueOf(number);

	}

	public static boolean isInvEmpty(ItemStack[] stack) {
		boolean empty = true;
		if(stack == null)
			return true;

		for(ItemStack i : stack) {
			if(i != null)
				return false;
		}

		return true;
	}

	/***
	 * Returns true if the array of object contains object2
	 */
	public static boolean doesArrayContains(Object[] object, Object object2) {
		for(Object obj : object) {
			if(obj.equals(object2))
				return true;
		}

		return false;
	}

	public static boolean doesArrayContains(int[] object, Object object2) {
		for(Object obj : object) {
			if(obj.equals(object2))
				return true;
		}
		return false;
	}

	public static boolean isInvEmpty(IInventory stack) {
		boolean empty = true;
		if(stack == null)
			return true;

		for(int i = 0; i < stack.getSizeInventory(); i++) {
			if(stack.getStackInSlot(i) != null)
				return false;
		}

		return true;
	}

	public static boolean doesInvHaveRoom(ItemStack item, IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i) == null || (item.isItemEqual(inv.getStackInSlot(i)) && inv.getStackInSlot(i).stackSize < inv.getInventoryStackLimit()))
				return true;
		}

		return false;
	}

	public static boolean hasFullStack(IInventory inv) {

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getMaxStackSize() == inv.getStackInSlot(i).stackSize)
				return true;
		}

		return false;
	}

	public static int numEmptySlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i) == null)
				num++;
		}

		return num;
	}

	public static int numFilledSlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).stackSize == inv.getStackInSlot(i).getMaxStackSize())
				num++;
		}

		return num;
	}

	public static void mergeInventory(ItemStack[] a, IInventory b) {
		for(int i = 0; i < a.length; i++) {
			int firstEmtpySlot = -1;
			int slot;

			if(a[i] != null) {
				for(slot = 0; slot < b.getSizeInventory(); slot++) {

					if(b.getStackInSlot(slot) == null) {
						if(firstEmtpySlot == -1)
							firstEmtpySlot = slot;
					}
					else if(b.getStackInSlot(slot).isItemEqual(a[i])) //b.isItemValidForSlot(slot, a[i]))//
					{
						int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).stackSize;

						if(a[i].stackSize < maxTransfer) {
							//chest.setInventorySlotContents(g, itemstack);
							b.getStackInSlot(slot).stackSize += a[i].stackSize;
							a[i] = null;
							break;
						}
						else {
							b.getStackInSlot(slot).stackSize = b.getInventoryStackLimit();
							a[i].stackSize -= maxTransfer;
						}
					}
				}

				if(a[i] != null && firstEmtpySlot != -1) {
					b.setInventorySlotContents(firstEmtpySlot, a[i].copy());
					a[i] = null;
				}
			}
		}
	}

	public static void mergeInventory(ItemStack a, IInventory b) {
		int firstEmtpySlot = -1;
		int slot;

		if(a != null) {
			for(slot = 0; slot < b.getSizeInventory(); slot++) {

				if(b.getStackInSlot(slot) == null) {
					if(firstEmtpySlot == -1)
						firstEmtpySlot = slot;
				}
				else if(b.getStackInSlot(slot).isItemEqual(a)) //b.isItemValidForSlot(slot, a[i]))//
				{
					int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).stackSize;

					if(a.stackSize < maxTransfer) {
						//chest.setInventorySlotContents(g, itemstack);
						b.getStackInSlot(slot).stackSize += a.stackSize;
						a = null;
						break;
					}
					else {
						b.getStackInSlot(slot).stackSize = b.getInventoryStackLimit();
						a.stackSize -= maxTransfer;
					}
				}
			}

			if(a != null && firstEmtpySlot != -1) {
				if(a.stackSize != 0)
					b.setInventorySlotContents(firstEmtpySlot, a.copy());
				a = null;
			}
		}
	}

	public static ItemStack getFirstItemInInv(ItemStack i[]) {
		for(ItemStack stack : i)
			if(stack != null) return stack;
		return null;
	}

	public static int getFirstFilledSlotIndex(IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
			if(inv.getStackInSlot(i) != null) return i;
		return inv.getSizeInventory();
	}

	public static int getContinuousBlockLength(World world, ForgeDirection direction, int startx, int starty, int startz, int maxDist, Block block) {
		int dist = 0;
		for(int i = 0; i < maxDist; i++) {
			if(world.getBlock(startx + (i*direction.offsetX), starty + (i*direction.offsetY), startz + (i*direction.offsetZ)) != block) 
				break;

			dist = i+1;
		}

		return dist;
	}

	public static boolean areOresSameTypeOreDict(ItemStack stack1, ItemStack stack2) {
		int[] stack1Id = OreDictionary.getOreIDs(stack1);
		int[] stack2Id = OreDictionary.getOreIDs(stack2);

		for(int i : stack1Id) {
			for(int j : stack2Id) {
				if(i == j)
					return true;
			}
		}

		return false;
	}
}
