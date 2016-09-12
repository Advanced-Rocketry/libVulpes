package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.interfaces.ILinkableTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePointer extends TileEntity implements IMultiblock, ILinkableTile {
	BlockPos masterBlockPos;

	TileEntity masterBlock;

	public TilePointer() {
		super();
		masterBlockPos = null;
	}

	public TilePointer(BlockPos pos) {
		super();
		masterBlockPos = pos;
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

	public boolean isSet() { return masterBlockPos != null;}

	public BlockPos getMasterPos() {
		return masterBlockPos;
	}

	public void setBlockPos(BlockPos pos) {
		masterBlockPos = pos;
	}

	@Override
	public void setMasterBlock(BlockPos pos) {
		setComplete(pos);
	}

	public TileEntity getFinalPointedTile() {
		TileEntity pointedTile = this.worldObj.getTileEntity(masterBlockPos);
		if(pointedTile == null)
			return null;
		else if(pointedTile instanceof TilePointer)
			return ((TilePointer)pointedTile).getFinalPointedTile();
		else
			return pointedTile;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound comp = new NBTTagCompound();

		writeToNBTHelper(comp);
		return new SPacketUpdateTileEntity(this.pos, 0, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		if(this.worldObj.isRemote)
		{
			readFromNBTHelper(pkt.getNbtCompound());
		}
	}


	@Override
	public boolean hasMaster() {
		return isSet();
	}

	@Override
	public TileEntity getMasterBlock() {
		if(hasMaster()) {
			if(masterBlock == null || masterBlock.isInvalid())
				masterBlock = this.worldObj.getTileEntity(masterBlockPos);
			return masterBlock;
		}
		return null;
	}

	public boolean canUpdate() {return false;}

	@Override
	public void setComplete(BlockPos pos) {
		masterBlockPos = pos;
	}

	@Override
	public void setIncomplete() {
		masterBlockPos = null;
		masterBlock = null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		return writeToNBTHelper(nbtTagCompound);
	}

	protected NBTTagCompound writeToNBTHelper(NBTTagCompound nbtTagCompound) {
		if(masterBlockPos != null) {
			nbtTagCompound.setInteger("masterX", masterBlockPos.getX());
			nbtTagCompound.setInteger("masterY", masterBlockPos.getY());
			nbtTagCompound.setInteger("masterZ", masterBlockPos.getZ());
		}
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		readFromNBTHelper(nbtTagCompound);
	}

	protected void readFromNBTHelper(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("masterX")) {
			masterBlockPos = new BlockPos(nbtTagCompound.getInteger("masterX"),
					nbtTagCompound.getInteger("masterY"),
					nbtTagCompound.getInteger("masterZ"));
		}
		else
			masterBlockPos = null;
	}
}
