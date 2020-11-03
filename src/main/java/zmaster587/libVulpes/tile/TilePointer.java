package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePointer extends TileEntity implements IMultiblock, ILinkableTile {
	BlockPos masterBlockPos;

	TileEntity masterBlock;

	public TilePointer() {
		super(LibVulpesTileEntityTypes.TILE_POINTER);
		masterBlockPos = null;
	}
	
	public TilePointer(BlockPos pos) {
		super(LibVulpesTileEntityTypes.TILE_POINTER);
		masterBlockPos = pos;
	}
	
	public TilePointer(TileEntityType<?> type) {
		super(type);
		masterBlockPos = null;
	}

	public TilePointer(TileEntityType<?> type, BlockPos pos) {
		super(type);
		masterBlockPos = pos;
	}

	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkStart(item, master, player, world);
			}
		}
		return false;
	}
	public boolean onLinkComplete(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkComplete(item, master, player, world);
			}
		}
		return false;
	}

	public boolean allowRedstoneOutputOnSide(Direction facing) {
		return true;
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
		TileEntity pointedTile;

		try  {
			if(world.isAreaLoaded(masterBlockPos, 1))
				pointedTile = this.world.getTileEntity(masterBlockPos);
			else
				pointedTile = null;
		} catch(NullPointerException e) {
			return null;
		}

		if(pointedTile == null)
			return null;
		else if(pointedTile instanceof TilePointer)
			try {
				return ((TilePointer)pointedTile).getFinalPointedTile();
			} catch (StackOverflowError e) {
				LibVulpes.logger.warn("Stack overflow has occured with tile at location " + getPos() + ".  The game has been prevented from crashing (in theory)");
				return null;
			}
		else
			return pointedTile;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.read(state, tag);
	}
	
	@Override
	public boolean hasMaster() {
		return isSet();
	}

	@Override
	public TileEntity getMasterBlock() {
		if(hasMaster()) {
			if(masterBlock == null || masterBlock.isRemoved())
				masterBlock = this.world.getTileEntity(masterBlockPos);
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
	public CompoundNBT write(CompoundNBT nbt) {
		CompoundNBT nbtTagCompound = super.write(nbt);

		return writeToNBTHelper(nbtTagCompound);
	}

	protected CompoundNBT writeToNBTHelper(CompoundNBT nbtTagCompound) {
		if(masterBlockPos != null) {
			nbtTagCompound.putInt("masterX", masterBlockPos.getX());
			nbtTagCompound.putInt("masterY", masterBlockPos.getY());
			nbtTagCompound.putInt("masterZ", masterBlockPos.getZ());
		}
		return nbtTagCompound;
	}

	
	//@Override
	public void read(BlockState state, CompoundNBT nbtTagCompound) {
		super.read(state, nbtTagCompound);

		readFromNBTHelper(nbtTagCompound);
	}

	protected void readFromNBTHelper(CompoundNBT nbtTagCompound) {
		if(nbtTagCompound.contains("masterX")) {
			masterBlockPos = new BlockPos(nbtTagCompound.getInt("masterX"),
					nbtTagCompound.getInt("masterY"),
					nbtTagCompound.getInt("masterZ"));
		}
		else
			masterBlockPos = null;
	}
}
