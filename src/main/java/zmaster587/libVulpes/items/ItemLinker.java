package zmaster587.libVulpes.items;

import java.util.List;

import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLinker extends Item {

	protected int linkX,linkY,linkZ, dimId;
	private final static int EMPTYSETTING = 0;

	public ItemLinker() {
		super();

		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
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

	@Deprecated
	public static int getMasterX(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		nbt = nbt.getCompoundTag("MasterPos");

		return nbt.getInteger("MasterX");
	}
	@Deprecated
	public static int getMasterY(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();

		if(nbt == null)
			return 0;

		nbt = nbt.getCompoundTag("MasterPos");

		return nbt.getInteger("MasterY");
	}
	@Deprecated
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
		NBTTagCompound nbt;
		if(!stack.hasTagCompound()) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		else {
			nbt = stack.getTagCompound();
		}
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.setInteger("MasterX", x);
		tag.setInteger("MasterY", y);
		tag.setInteger("MasterZ", z);
		nbt.setTag("MasterPos", tag);
	}
	
	public static void setMasterCoords(ItemStack stack, BlockPos pos) {
		setMasterCoords(stack, pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockPos getMasterCoords(ItemStack stack ) {
		if(!stack.hasTagCompound()) {

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("MasterPos", new NBTTagCompound());
			stack.setTagCompound(nbt);
		}
		
		return new BlockPos(getMasterX(stack),getMasterY(stack),getMasterZ(stack));
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world,
			EntityPlayer player, EnumHand hand) {

		//if(player.isSneaking()) {
			//resetPosition(stack);
		//}
		return super.onItemRightClick(stack, world, player, hand);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		
		TileEntity entity = worldIn.getTileEntity(pos);

		if(entity != null) {
			if(entity instanceof ILinkableTile) {
				applySettings(stack, (ILinkableTile)entity, playerIn, worldIn);
				return EnumActionResult.FAIL;
			}
		}
		else if(playerIn.isSneaking()) {
			resetPosition(stack);
		}

		return EnumActionResult.FAIL;
	}

	protected void applySettings(ItemStack itemStack, ILinkableTile pad, EntityPlayer player, World world) {
		if(!isSet(itemStack)) 
			pad.onLinkStart(itemStack, (TileEntity)pad, player, world);
		else
			pad.onLinkComplete(itemStack, (TileEntity)pad, player, world);
	}
}
