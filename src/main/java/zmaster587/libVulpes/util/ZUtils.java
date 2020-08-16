package zmaster587.libVulpes.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

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
		
		public void write(CompoundNBT tag) {
			tag.putByte("redstoneState", (byte)this.ordinal());
		}
		
		public static RedstoneState createFromNBT(CompoundNBT tag) {
			return RedstoneState.values()[tag.getByte("redstoneState")];
		}
	}

	public static int getAverageColor(long r, long g, long b, int total) {
		return (int)(( (r/total) ) | ( (g/total) << 8 ) | ( ( b/total ) << 16 ) );
	}

	public static int getDirectionFacing(float rotationYaw) {
		int l = MathHelper.floor((double)(MathHelper.wrapDegrees(rotationYaw) * 4.0F / 360.0F) + 0.5D) & 3;

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

		AxisAlignedBB ret = new AxisAlignedBB(minX,
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


		return new AxisAlignedBB(e.getPosX() + rotatedLocal.minX, e.getPosY() + rotatedLocal.minY, e.getPosZ() + rotatedLocal.minZ, rotatedLocal.maxX + e.getPosX(), rotatedLocal.maxY + e.getPosY(), rotatedLocal.maxZ + e.getPosZ());
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
			if(!i.isEmpty())
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
			if(!stack.getStackInSlot(i).isEmpty())
				return false;
		}

		return true;
	}

	public static boolean doesInvHaveRoom(ItemStack item, IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i).isEmpty() || (item.isItemEqual(inv.getStackInSlot(i)) && inv.getStackInSlot(i).getCount() < inv.getInventoryStackLimit()))
				return true;
		}

		return false;
	}

	public static boolean hasFullStack(IInventory inv) {

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getMaxStackSize() == inv.getStackInSlot(i).getCount())
				return true;
		}

		return false;
	}

	public static int numEmptySlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i).isEmpty())
				num++;
		}

		return num;
	}

	public static int numFilledSlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getCount() == inv.getStackInSlot(i).getMaxStackSize())
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

					if(b.getStackInSlot(slot).isEmpty()) {
						if(firstEmtpySlot == -1)
							firstEmtpySlot = slot;
					}
					else if(b.getStackInSlot(slot).isItemEqual(a[i])) //b.isItemValidForSlot(slot, a[i]))//
					{
						int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).getCount();

						if(a[i].getCount() < maxTransfer) {
							//chest.setInventorySlotContents(g, itemstack);
							b.getStackInSlot(slot).setCount(b.getStackInSlot(slot).getCount() + a[i].getCount());
							a[i] = ItemStack.EMPTY;
							break;
						}
						else {
							b.getStackInSlot(slot).setCount(b.getInventoryStackLimit());
							a[i].setCount(a[i].getCount() - maxTransfer);
						}
					}
				}

				if(!a[i].isEmpty() && firstEmtpySlot != -1) {
					b.setInventorySlotContents(firstEmtpySlot, a[i].copy());
					a[i] = ItemStack.EMPTY;
				}
			}
		}
	}

	public static void mergeInventory(ItemStack a, IInventory b) {
		int firstEmtpySlot = -1;
		int slot;

		if(a != null) {
			for(slot = 0; slot < b.getSizeInventory(); slot++) {

				if(b.getStackInSlot(slot).isEmpty()) {
					if(firstEmtpySlot == -1)
						firstEmtpySlot = slot;
				}
				else if(b.getStackInSlot(slot).isItemEqual(a)) //b.isItemValidForSlot(slot, a[i]))//
				{
					int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).getCount();

					if(a.getCount() < maxTransfer) {
						//chest.setInventorySlotContents(g, itemstack);
						b.getStackInSlot(slot).setCount(b.getStackInSlot(slot).getCount() + a.getCount());
						a = ItemStack.EMPTY;
						break;
					}
					else {
						b.getStackInSlot(slot).setCount( b.getInventoryStackLimit());
						a.setCount( a.getCount() - maxTransfer);
					}
				}
			}

			if(!a.isEmpty() && firstEmtpySlot != -1) {
				if(a.getCount() != 0)
					b.setInventorySlotContents(firstEmtpySlot, a.copy());
				a =  ItemStack.EMPTY;
			}
		}
	}

	public static ItemStack getFirstItemInInv(ItemStack i[]) {
		for(ItemStack stack : i)
			if(!stack.isEmpty()) return stack;
		return null;
	}

	public static int getFirstFilledSlotIndex(IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
			if(inv.getStackInSlot(i) != ItemStack.EMPTY) return i;
		return inv.getSizeInventory();
	}

	public static int getContinuousBlockLength(World world, Direction direction, BlockPos pos, int maxDist, Block block) {
		int dist = 0;
		for(int i = 0; i < maxDist; i++) {
			if(world.getBlockState(new BlockPos(pos.add((i*direction.getXOffset()), (i*direction.getYOffset()), (i*direction.getZOffset())))).getBlock() != block) 
				break;

			dist = i+1;
		}

		return dist;
	}
	
	public static int getContinuousBlockLength(World world, Direction direction, BlockPos pos, int maxDist, Block[] blocks) {
		int dist = 0;
		for(int i = 0; i < maxDist; i++) {
			Block blockchecked = world.getBlockState(new BlockPos(pos.add((i*direction.getXOffset()), (i*direction.getYOffset()), (i*direction.getZOffset())))).getBlock();
			boolean exists = false;
			for( Block b : blocks ) {
				if(blockchecked == b) {
					exists = true;
					break;
				}
			}
			
			if(!exists) 
				break;

			dist = i+1;
		}

		return dist;
	}

	public static boolean areOresSameTypeOreDict(ItemStack stack1, ItemStack stack2) {
		
		Collection<ResourceLocation> item1s = ItemTags.getCollection().getOwningTags(stack1.getItem());
		Collection<ResourceLocation> item2s = ItemTags.getCollection().getOwningTags(stack2.getItem());
		
		return item1s.stream().anyMatch(value -> item2s.contains(value));
	}
	
	public static ResourceLocation getDimensionIdentifier(World world)
	{
		return DynamicRegistries.func_239770_b_().func_230520_a_().getKey(getDimensionType(world));
	}
	
	public static Optional<DimensionType> getDimensionFromIdentifier(ResourceLocation location)
	{
		return DynamicRegistries.func_239770_b_().func_230520_a_().func_241873_b(location);
	}
	
	public static DimensionType getDimensionType(World world)
	{
		return world.func_230315_m_();
	}
	
	public static ServerWorld getWorld(ResourceLocation worldLoc)
	{
		RegistryKey<World> registrykey = RegistryKey.func_240903_a_(Registry.field_239699_ae_, worldLoc);
		
		return ServerLifecycleHooks.getCurrentServer().getWorld(registrykey);
	}
	
	public static boolean isWorldLoaded(ResourceLocation worldLoc)
	{
		RegistryKey<World> registrykey = RegistryKey.func_240903_a_(Registry.field_239699_ae_, worldLoc);
		
		return registrykey != null;
	}
	
	public static boolean isWorldRegistered(ResourceLocation worldLoc)
	{
		RegistryKey<World> registrykey = RegistryKey.func_240903_a_(Registry.field_239699_ae_, worldLoc);
		
		return registrykey != null;
	}
	
	public static boolean unegisterWorld(ResourceLocation worldLoc)
	{
		//XXX
		return false;
	}
	
	
	public static boolean RegisterWorld(ResourceLocation worldLoc, DimensionType type)
	{
		//XXX
		return false;
	}
	public static int getDimensionId(World world)
	{
		return getDimensionType(world).func_241513_m_();
	}

	public static void initDimension(ResourceLocation dimid) {
		// TODO Auto-generated method stub
		
	}
	
	public static Direction rotateAround(Direction a, Direction b)
	{
		Vector3f newdir = new Vector3f(a.getDirectionVec().getX(), a.getDirectionVec().getY(), a.getDirectionVec().getZ());
		newdir.transform(b.getRotation());
		
		return Direction.getFacingFromVector(newdir.getX(), newdir.getY(), newdir.getZ());
	}
}
