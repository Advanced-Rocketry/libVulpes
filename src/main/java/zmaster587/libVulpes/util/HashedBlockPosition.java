package zmaster587.libVulpes.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class HashedBlockPosition  {
	public int x,z;
	public short y;
	
	
	public HashedBlockPosition(int x, int y,int z) {
		this.x = x;
		this.y = (short)y;
		this.z = z;
	}
	
	public HashedBlockPosition(BlockPos pos) {
		this.x = pos.getX();
		this.y = (short)pos.getY();
		this.z = pos.getZ();
	}
	
	public BlockPos getBlockPos() {
		return new BlockPos(x,y,z);
	}
	
	/**
	 * @param dx x offset
	 * @param dy y offset
	 * @param dz z offset
	 * @return a new object containing the coordinates of that offset
	 */
	public HashedBlockPosition getPositionAtOffset(int dx, int dy, int dz) {
		return new HashedBlockPosition(dx + x, dy + y, dz + z);
	}
	
	public HashedBlockPosition getPositionAtOffset(EnumFacing facing) {
		return new HashedBlockPosition(facing.getFrontOffsetX() + x, facing.getFrontOffsetY() + y, facing.getFrontOffsetZ() + z);
	}
	
	public double getDistance(HashedBlockPosition otherPos) {
		return Math.sqrt(Math.pow(x-otherPos.x, 2) + Math.pow(y-otherPos.y, 2) + Math.pow(z-otherPos.z, 2));
	}
	
	@Override
	public int hashCode() {
		return (x*179425423) ^ (z*179426549)  ^ (y*179424691);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof HashedBlockPosition) {
			return this.x == ((HashedBlockPosition) obj).x && this.y == ((HashedBlockPosition) obj).y && this.z == ((HashedBlockPosition) obj).z;
		}
		
		return super.equals(obj);
	}
}
