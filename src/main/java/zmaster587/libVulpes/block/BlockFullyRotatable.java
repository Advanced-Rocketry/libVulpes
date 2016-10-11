package zmaster587.libVulpes.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
public class BlockFullyRotatable extends RotatableBlock {

	public BlockFullyRotatable(Material par2Material) {
		super(par2Material);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		if(Math.abs(entity.rotationPitch) > 60) {
			world.setBlockMetadataWithNotify(x, y, z, entity.rotationPitch > 0 ? ForgeDirection.UP.ordinal() : ForgeDirection.DOWN.ordinal(), 2);
		}
		else
			super.onBlockPlacedBy(world, x, y, z, entity, stack);
	}

	public static ForgeDirection getRelativeSide(int side, int meta) {
		return getRelativeSide(ForgeDirection.getOrientation(side), meta & 7);
	}

	//North is defined as the front
	public static ForgeDirection getRelativeSide(ForgeDirection side, int meta) {

		ForgeDirection dir = ForgeDirection.getOrientation(meta & 7);

		if(dir == ForgeDirection.NORTH)
			return side;
		else if(dir == ForgeDirection.SOUTH)
			return side.getOpposite();
		else if(dir == ForgeDirection.EAST)
			return side.getRotation(ForgeDirection.DOWN);
		else if(dir == ForgeDirection.WEST)
			return side.getRotation(ForgeDirection.UP);
		else if(dir == ForgeDirection.UP)
			return side.getRotation(ForgeDirection.EAST);
		else if (dir == ForgeDirection.DOWN)
			return side.getRotation(ForgeDirection.WEST);	


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
			return this.rear;
		else if(side == ForgeDirection.WEST)
			return this.sides;

		return blockIcon;
	}
}
