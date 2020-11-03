package zmaster587.libVulpes.tile;


import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

public abstract class TileEntityMachine extends TileEntity implements ISidedInventory, ITickableTileEntity,  IToggleableMachine {
	
	
	public TileEntityMachine(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	protected UniversalBattery energy;
	
	protected ItemStack inv[];
	
	protected int progress, totalTime;
	
	public int getProgressTime() { return progress; }
	
	public void setProgressTime(int time) { progress = time; }
	
	public int getTotalProgressTime() { return totalTime; }
	
	public void setTotalProgressTime(int time) { totalTime = time; }
	
	protected void setRunning(boolean on, World world) {
		//TODO set block state
		/*if(on)
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) | 8, 2);
		else
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & (~8), 2);*/
	}
	
	protected boolean getIsRunning() {
		return this.getBlockState().get(BlockTile.STATE);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt = this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}
	
	@Override 
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		ListNBT list = new ListNBT();

		ListNBT itemList = new ListNBT();
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];

			if(stack != null) {
				CompoundNBT tag = new CompoundNBT();
				tag.putByte("Slot", (byte)(i));
				stack.write(tag);
				itemList.add(tag);
			}
		}
		nbt.put("Inventory", itemList);
		
		nbt.putInt("progress", progress);
		nbt.putInt("totalTime", totalTime);
		
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		ListNBT tagList = nbt.getList("Inventory", (byte)10);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT tag = (CompoundNBT) tagList.getCompound(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.read(tag);
			}
		}
		
		progress = nbt.getInt("progress");
		totalTime = nbt.getInt("totalTime");
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
		else if(inv[i].getCount() + amt > inv[i].getMaxStackSize())
			inv[i].setCount(inv[i].getMaxStackSize());
		else
			inv[i].setCount(inv[i].getCount() + amt);
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack ret;
		if(inv[i] == null)
			ret = null;
		else if(inv[i].getCount() > j) {
			ret = inv[i].split(j);
		}
		else {
			ret = inv[i].copy();
			inv[i] = null;
		}
		return ret;
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
	public boolean isUsableByPlayer(PlayerEntity entityplayer) {
		return entityplayer.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) < 64;
	}
}