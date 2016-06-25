package zmaster587.libVulpes.tile;

import net.minecraft.tileentity.TileEntity;

public interface IMultiblock {
	
	public boolean hasMaster();
	
	public TileEntity getMasterBlock();
	
	public void setComplete(int x, int y, int z);
	
	public void setIncomplete();
	
	public void setMasterBlock(int x, int y, int z);
}
