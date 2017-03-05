package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.interfaces.ILinkableTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePointer extends TileEntity implements IMultiblock, ILinkableTile {
	int masterX, masterY, masterZ;

	TileEntity masterBlock;
	
	public TilePointer() {
		super();
		masterX = 0;
		masterY = -1;
		masterZ = 0;
	}

	public TilePointer(int x, int y, int z) {
		super();
		masterX = x;
		masterY = y;
		masterZ = z;
	}

	public boolean onLinkStart(ItemStack item, TileEntity entity, EntityPlayer player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkStart(item, master, player, world);
			}
		}
		return false;
	}
	public boolean onLinkComplete(ItemStack item, TileEntity entity, EntityPlayer player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkComplete(item, master, player, world);
			}
		}
		return false;
	}
	

	public boolean allowRedstoneOutputOnSide(ForgeDirection facing) {
		return true;
	}

	public boolean isSet() { return masterY != -1;}

	public int getX() {return masterX;}
	public int getY() {return masterY;}
	public int getZ() {return masterZ;}

	public void setX(int x) {masterX = x;}
	public void setY(int y) {masterY = y;}
	public void setZ(int z) {masterZ = z;}

	public void setMasterBlock(int x, int y, int z) {
		setComplete(x, y, z);
	}

	public TileEntity getFinalPointedTile() {
		TileEntity pointedTile = this.worldObj.getTileEntity(masterX, masterY, masterZ);
		if(pointedTile == null)
			return null;
		else if(pointedTile instanceof TilePointer)
			return ((TilePointer)pointedTile).getFinalPointedTile();
		else
			return pointedTile;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound comp = new NBTTagCompound();

		writeToNBTHelper(comp);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		if(this.worldObj.isRemote)
		{
			readFromNBTHelper(pkt.func_148857_g());
		}
	}


	@Override
	public boolean hasMaster() {
		// TODO Auto-generated method stub
		return masterY != -1;
	}

	@Override
	public TileEntity getMasterBlock() {
		if(hasMaster()) {
			if(masterBlock == null || masterBlock.isInvalid())
				masterBlock = this.worldObj.getTileEntity(masterX, masterY, masterZ);
			return masterBlock;
		}
		return null;
	}

	public boolean canUpdate() {return false;}

	@Override
	public void setComplete(int x, int y, int z) {
		this.masterX = x;
		this.masterY = y;
		this.masterZ = z;

	}

	@Override
	public void setIncomplete() {
		this.masterX = -1;
		this.masterY = -1;
		this.masterZ = -1;
		masterBlock = null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		writeToNBTHelper(nbtTagCompound);
	}

	protected void writeToNBTHelper(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setInteger("masterX", masterX);
		nbtTagCompound.setInteger("masterY", masterY);
		nbtTagCompound.setInteger("masterZ", masterZ);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		readFromNBTHelper(nbtTagCompound);
	}

	protected void readFromNBTHelper(NBTTagCompound nbtTagCompound) {
		masterX = nbtTagCompound.getInteger("masterX");
		masterY = nbtTagCompound.getInteger("masterY");
		masterZ = nbtTagCompound.getInteger("masterZ");
	}
}
