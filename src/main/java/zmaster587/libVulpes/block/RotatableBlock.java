package zmaster587.libVulpes.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RotatableBlock extends Block {

	protected IIcon front, back, sides, top, bottom;

	public RotatableBlock(Material par2Material) {
		super(par2Material);
	}
	
	
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int l = MathHelper.floor_double((double)(MathHelper.wrapAngleTo180_float(entity.rotationYaw) * 4.0F / 360.0F) + 0.5D) & 3;
		
		if(l == 0)
			l = 2;
		else if(l == 1)
			l = 5;
		else if(l == 2)
			l = 3;
		else
			l = 4;
		
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}
	
	public static ForgeDirection getFront(int meta) {
		return ForgeDirection.getOrientation(meta & 7);
	}
	
	public static ForgeDirection getRelativeSide(int side, int meta) {
		return getRelativeSide(ForgeDirection.getOrientation(side), meta & 7);
	}
	
	//North is defined as the front
	public static ForgeDirection getRelativeSide(ForgeDirection side, int meta) {
		
		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
			return side;
		
		ForgeDirection dir = ForgeDirection.getOrientation(meta & 7);
		
		if(dir == ForgeDirection.NORTH)
			return side;
		else if(dir == ForgeDirection.SOUTH)
			return side.getOpposite();
		else if(dir == ForgeDirection.EAST)
			return side.getRotation(ForgeDirection.DOWN);
		else if(dir == ForgeDirection.WEST)
			return side.getRotation(ForgeDirection.UP);
					
		
		return side.getOpposite();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int dir, int meta) {
		
		ForgeDirection side = getRelativeSide(dir,meta);
		
		if(side == ForgeDirection.UP)
			return this.top;
		else if(side == ForgeDirection.DOWN)
			return this.bottom;
		else if(side == ForgeDirection.NORTH)
			return this.front;
		else if(side == ForgeDirection.EAST)
			return this.sides;
		else if(side == ForgeDirection.SOUTH)
			return this.back;
		else if(side == ForgeDirection.WEST)
			return this.sides;
		
		return blockIcon;
	}
}
