package zmaster587.libVulpes.items;

import java.util.List;

import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemLinker extends Item {

	protected int linkX,linkY,linkZ, dimId;
	private final static int EMPTYSETTING = 0;

	public ItemLinker() {
		super();

		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabTransport);
		dimId = 0;
	}


	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		int y = getMasterY(par1ItemStack);

		if(y == 0){
			par3List.add("Coords unset!");
		}
		else {
			par3List.add("X: " + getMasterX(par1ItemStack));
			par3List.add("Y: " + getMasterY(par1ItemStack));
			par3List.add("Z: " + getMasterZ(par1ItemStack));
			int dimId = getDimId(par1ItemStack);
			if(dimId != -1)
				par3List.add("Dim: " + dimId);
		}
	}

	public static boolean isSet(ItemStack stack) {
		return getMasterY(stack) != 0;
	}

	public static int getMasterX(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		return nbt.getInteger("MasterX");
	}
	public static int getMasterY(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();

		if(nbt == null)
			return 0;

		nbt = nbt.getCompoundTag("MasterPos");

		return nbt.getInteger("MasterY");
	}
	public static int getMasterZ(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		return nbt.getInteger("MasterZ");
	}
	
	public static void setDimId(ItemStack itemStack, int id) {
		NBTTagCompound nbt;
		if(!itemStack.hasTagCompound()) {
			nbt = new NBTTagCompound();
			itemStack.setTagCompound(nbt);
		}
		else
			nbt = itemStack.getTagCompound();
		
		nbt.setInteger("dimId", id);
		
	}
	
	public static int getDimId(ItemStack itemStack) {
		NBTTagCompound nbt;
		if(!itemStack.hasTagCompound()) {
			nbt = new NBTTagCompound();
		}
		else
			nbt = itemStack.getTagCompound();
		
		return nbt.hasKey("dimId") ? nbt.getInteger("dimId") : -1;
		
	}

	public static void setMasterX(ItemStack itemStack, int num) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		nbt.setInteger("MasterX", num);
	}

	public static void setMasterY(ItemStack itemStack, int num) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		nbt.setInteger("MasterY", num);
	}

	public static void setMasterZ(ItemStack itemStack, int num) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		nbt.setInteger("MasterZ", num);
	}

	public static void setMasterCoords(ItemStack stack , int x, int y, int z) {
		if(!stack.hasTagCompound()) {

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("MasterPos", new NBTTagCompound());
			stack.setTagCompound(nbt);
		}
		setMasterX(stack, x);
		setMasterY(stack, y);
		setMasterZ(stack, z);
	}

	public static void resetPosition(ItemStack itemStack) {
		NBTTagCompound position = new NBTTagCompound();

		position.setInteger("MasterX", EMPTYSETTING);
		position.setInteger("MasterY", EMPTYSETTING);
		position.setInteger("MasterZ", EMPTYSETTING);
		position.setInteger("dimId", -1);

		itemStack.setTagInfo("MasterPos", position);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {

		if(player.isSneaking()) {
			resetPosition(stack);
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		TileEntity entity = world.getTileEntity(par4, par5, par6);

		if(entity != null) {
			if(entity instanceof ILinkableTile) {
				applySettings(itemStack, (ILinkableTile)entity, player, world);
				return true;
			}
		}
		else if(player.isSneaking()) {
			resetPosition(itemStack);
			return true;
		}

		return false;
	}

	protected void applySettings(ItemStack itemStack, ILinkableTile pad, EntityPlayer player, World world) {
		if(!isSet(itemStack)) 
			pad.onLinkStart(itemStack, (TileEntity)pad, player, world);
		else
			pad.onLinkComplete(itemStack, (TileEntity)pad, player, world);
	}


	public static BlockPosition getMasterCoords(ItemStack item) {
		return new BlockPosition(getMasterX(item), getMasterY(item), getMasterZ(item));
	}
}
