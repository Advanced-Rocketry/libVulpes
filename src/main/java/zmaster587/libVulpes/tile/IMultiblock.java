package zmaster587.libVulpes.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IMultiblock {
	
	public boolean hasMaster();
	
	public TileEntity getMasterBlock();
	
	public void setComplete(BlockPos pos);
	
	public void setIncomplete();
	
	public void setMasterBlock(BlockPos pos);
}
