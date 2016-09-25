package zmaster587.libVulpes.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.ZUtils;

//Used to store info about the block previously at the location
public class TilePlaceholder extends TilePointer {
	IBlockState replacedState;
	TileEntity replacedTile;
	
	public IBlockState getReplacedState() {
		if(replacedState == null)
			return Blocks.AIR.getDefaultState();
		return replacedState;
	}
	
	public void setReplacedBlockState(IBlockState state) {
		this.replacedState = state;
	}
	
	public TileEntity getReplacedTileEntity() {
		return replacedTile;
	}
	
	public int getReplacedMeta() {
		return replacedState.getBlock().getMetaFromState(replacedState);
	}
	
	public void setReplacedTileEntity(TileEntity tile) {
		replacedTile = tile;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();

		return writeToNBT(nbt);
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		readFromNBT(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setInteger("ID", Block.getIdFromBlock(getReplacedState().getBlock()));
		nbt.setInteger("damage", getReplacedState().getBlock().getMetaFromState(getReplacedState()));
		NBTTagCompound tag = new NBTTagCompound();
		
		if(replacedTile != null) {
			replacedTile.writeToNBT(tag);
			nbt.setTag("tile", tag);
		}
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		//TODO: perform sanity check
		replacedState = Block.getBlockById(nbt.getInteger("ID")).getDefaultState();
		replacedState = replacedState.getBlock().getStateFromMeta(nbt.getInteger("damage"));
		
		if(nbt.hasKey("tile")) {
			NBTTagCompound tile = nbt.getCompoundTag("tile");
			
			replacedTile = ZUtils.createTile(tile);
		}
	}
}
