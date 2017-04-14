package zmaster587.libVulpes.util;

public class BlockPosition  {
	public int x,z;
	public short y;
	
	
	public BlockPosition(int x, int y,int z) {
		this.x = x;
		this.y = (short)y;
		this.z = z;
	}
	
	/**
	 * @param dx x offset
	 * @param dy y offset
	 * @param dz z offset
	 * @return a new object containing the coordinates of that offset
	 */
	public BlockPosition getPositionAtOffset(int dx, int dy, int dz) {
		return new BlockPosition(dx + x, dy + y, dz + z);
	}
	
	public double getDistance(BlockPosition otherPos) {
		return Math.sqrt(Math.pow(x-otherPos.x, 2) + Math.pow(y-otherPos.y, 2) + Math.pow(z-otherPos.z, 2));
	}
	
	@Override
	public int hashCode() {
		return (x*179425423) ^ (z*179426549)  ^ (y*179424691);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof BlockPosition) {
			return this.x == ((BlockPosition) obj).x && this.y == ((BlockPosition) obj).y && this.z == ((BlockPosition) obj).z;
		}
		
		return super.equals(obj);
	}
}
