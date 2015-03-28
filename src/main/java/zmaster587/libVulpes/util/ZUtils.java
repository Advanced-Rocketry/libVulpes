package zmaster587.libVulpes.util;

import net.java.games.input.Component.Identifier.Axis;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class ZUtils {


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
				b.setInventorySlotContents(firstEmtpySlot, a.copy());
				a = null;
			}
		}
	}

	public static ItemStack getFirstFilledSlot(ItemStack i[]) {
		for(ItemStack stack : i)
			if(stack != null) return stack;
		return null;
	}
}
