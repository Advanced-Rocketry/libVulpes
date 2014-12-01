package zmaster587.libVulpes.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class RotatableMachineBlock extends RotatableBlock {
	protected IIcon activeFront;
	protected final Random random = new Random();

	public RotatableMachineBlock(Material par2Material) {
		super(par2Material);
		this.isBlockContainer = true;
	}
	
    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		IInventory tileentitychest = (IInventory)world.getTileEntity(x, y, z);

	        if (tileentitychest != null)
	        {
	            for (int j1 = 0; j1 < tileentitychest.getSizeInventory(); ++j1)
	            {
	                ItemStack itemstack = tileentitychest.getStackInSlot(j1);

	                if (itemstack != null)
	                {
	                    float f = this.random.nextFloat() * 0.8F + 0.1F;
	                    float f1 = this.random.nextFloat() * 0.8F + 0.1F;
	                    EntityItem entityitem;

	                    for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
	                    {
	                        int k1 = this.random.nextInt(21) + 10;

	                        if (k1 > itemstack.stackSize)
	                        {
	                            k1 = itemstack.stackSize;
	                        }

	                        itemstack.stackSize -= k1;
	                        entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
	                        float f3 = 0.05F;
	                        entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
	                        entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
	                        entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);

	                        if (itemstack.hasTagCompound())
	                        {
	                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
	                        }
	                    }
	                }
	            }
	        }

	        super.breakBlock(world, x, y, z, par5, par6);
	}

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
    
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int dir, int meta) {
		
		ForgeDirection side = getRelativeSide(dir,meta & 7);
		
		if(side == ForgeDirection.UP)
			return this.top;
		else if(side == ForgeDirection.DOWN)
			return this.bottom;
		else if(side == ForgeDirection.NORTH) {
			if((meta & 8) != 0)
				return this.activeFront;
			else
				return this.front;
		}
		else if(side == ForgeDirection.EAST)
			return this.sides;
		else if(side == ForgeDirection.SOUTH)
			return this.back;
		else if(side == ForgeDirection.WEST)
			return this.sides;
		
		return blockIcon;
	}
}
