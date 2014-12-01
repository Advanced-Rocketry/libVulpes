package zmaster587.libVulpes.tile;

import net.minecraft.tileentity.TileEntity;

public interface IMultiblock {
	
	public boolean isComplete();
	
	public TileEntity getMasterBlock();
	
	public void setComplete(int x, int y, int z);
	
	public void setIncomplete();
}
