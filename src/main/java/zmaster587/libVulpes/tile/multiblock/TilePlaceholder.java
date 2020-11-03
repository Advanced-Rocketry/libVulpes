package zmaster587.libVulpes.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.tile.TilePointer;

//Used to store info about the block previously at the location
public class TilePlaceholder extends TilePointer {
	public TilePlaceholder(TileEntityType<?> type) {
		super(type);
	}
	
	public TilePlaceholder() {
		super(LibVulpesTileEntityTypes.TILE_PLACEHOLDER);
	}

	BlockState replacedState;
	TileEntity replacedTile;
	
	public BlockState getReplacedState() {
		if(replacedState == null)
			return Blocks.AIR.getDefaultState();
		return replacedState;
	}
	
	public void setReplacedBlockState(BlockState state) {
		this.replacedState = state;
	}
	
	public TileEntity getReplacedTileEntity() {
		return replacedTile;
	}
	
	public void setReplacedTileEntity(TileEntity tile) {
		replacedTile = tile;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();

		write(nbt);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();

		return write(nbt);
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
	}
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		deserializeNBT(nbt);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putInt("ID", Block.getStateId(getReplacedState()));
		CompoundNBT tag = new CompoundNBT();
		
		if(replacedTile != null) {
			replacedTile.write(tag);
			nbt.put("tile", tag);
		}
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		//TODO: perform sanity check
		replacedState = Block.getStateById(nbt.getInt("ID"));
		
		if(nbt.contains("tile")) {
			CompoundNBT tile = nbt.getCompound("tile");
			replacedTile = TileEntity.readTileEntity(replacedState, tile);
		}
	}
}
