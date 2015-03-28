package zmaster587.libVulpes.tile;


import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityMachine extends TileEntity implements ISidedInventory  {
	
	
	protected EnergyStorage energy;
	
	protected ItemStack inv[];
	
	protected int progress, totalTime;
	
	public int getProgressTime() { return progress; }
	
	public void setProgressTime(int time) { progress = time; }
	
	public int getTotalProgressTime() { return totalTime; }
	
	public void setTotalProgressTime(int time) { totalTime = time; }
	
	protected void setRunning(boolean on, World world) {
		if(on)
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) | 8, 2);
		else
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & (~8), 2);
	}
	
	protected boolean getIsRunning() {
		return (this.blockMetadata & 8) == 8;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}
	
	@Override 
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];

			if(stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		nbt.setTag("Inventory", itemList);
		
		nbt.setInteger("progress", progress);
		nbt.setInteger("totalTime", totalTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		NBTTagList tagList = nbt.getTagList("Inventory", (byte)10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		
		progress = nbt.getInteger("progress");
		totalTime = nbt.getInteger("totalTime");
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}
	
	public void incStackSize(int i, int amt) {

		if(inv[i] == null)
			return;
		else if(inv[i].stackSize + amt > inv[i].getMaxStackSize())
			inv[i].stackSize = inv[i].getMaxStackSize();
		else
			inv[i].stackSize += amt;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack ret;
		if(inv[i] == null)
			ret = null;
		else if(inv[i].stackSize > j) {
			ret = inv[i].splitStack(j);
		}
		else {
			ret = inv[i].copy();
			inv[i] = null;
		}
		return ret;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inv[i];
	}
	
	public abstract void onInventoryUpdate();
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;

		//if(!this.worldObj.isRemote)
			onInventoryUpdate();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(this.xCoord, this.yCoord, this.zCoord) < 64;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	
	@Override
	public boolean hasCustomInventoryName() { return false; }
}