package zmaster587.libVulpes.items;

import java.util.List;

import zmaster587.libVulpes.interfaces.ILinkableTile;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import zmaster587.libVulpes.interfaces.ILinkableTile;

import java.util.List;

public class ItemLinker extends Item {

	protected int linkX,linkY,linkZ, dimId;
	private final static int EMPTYSETTING = 0;

	public ItemLinker(Properties properties) {
		super(properties);
		properties.maxStackSize(1);
		properties.group(ItemGroup.TRANSPORTATION);
		dimId = 0;
	}


	@Override
	public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List par3List, ITooltipFlag par4)
	{
		int y = getMasterY(par1ItemStack);

		if(y == 0){
			par3List.add(new StringTextComponent("Coords unset!"));
		}
		else {
			par3List.add(new StringTextComponent("X: " + getMasterX(par1ItemStack)));
			par3List.add(new StringTextComponent("Y: " + getMasterY(par1ItemStack)));
			par3List.add(new StringTextComponent("Z: " + getMasterZ(par1ItemStack)));
			ResourceLocation dimId = getDimId(par1ItemStack);
			if(dimId != null)
				par3List.add(new StringTextComponent("Dim: " + dimId.getPath()));
		}
	}

	public static boolean isSet(ItemStack stack) {
		return getMasterY(stack) != 0;
	}

	@Deprecated
	public static int getMasterX(ItemStack itemStack) {
		CompoundNBT nbt = itemStack.getTag();
		nbt = nbt.getCompound("MasterPos");

		return nbt.getInt("MasterX");
	}
	@Deprecated
	public static int getMasterY(ItemStack itemStack) {
		CompoundNBT nbt = itemStack.getTag();

		if(nbt == null)
			return 0;

		nbt = nbt.getCompound("MasterPos");

		return nbt.getInt("MasterY");
	}
	@Deprecated
	public static int getMasterZ(ItemStack itemStack) {
		CompoundNBT nbt = itemStack.getTag();
		nbt = nbt.getCompound("MasterPos");

		return nbt.getInt("MasterZ");
	}
	
	public static void setDimId(ItemStack itemStack, ResourceLocation id) {
		CompoundNBT nbt;
		if(!itemStack.hasTag()) {
			nbt = new CompoundNBT();
			itemStack.setTag(nbt);
		}
		else
			nbt = itemStack.getTag();
		
		nbt.putString("dimId", id.toString());
		
	}
	
	public static ResourceLocation getDimId(ItemStack itemStack) {
		CompoundNBT nbt;
		if(!itemStack.hasTag()) {
			nbt = new CompoundNBT();
		}
		else
			nbt = itemStack.getTag();
		
		return nbt.contains("dimId") ? new ResourceLocation(nbt.getString("dimId")) : null;
		
	}

	public static void setMasterX(ItemStack itemStack, int num) {
		CompoundNBT nbt = itemStack.getTag();
		nbt = nbt.getCompound("MasterPos");

		nbt.putInt("MasterX", num);
	}

	public static void setMasterY(ItemStack itemStack, int num) {
		CompoundNBT nbt = itemStack.getTag();
		nbt = nbt.getCompound("MasterPos");

		nbt.putInt("MasterY", num);
	}

	public static void setMasterZ(ItemStack itemStack, int num) {
		CompoundNBT nbt = itemStack.getTag();
		nbt = nbt.getCompound("MasterPos");

		nbt.putInt("MasterZ", num);
	}

	public static void setMasterCoords(ItemStack stack , int x, int y, int z) {
		CompoundNBT nbt;
		if(!stack.hasTag()) {
			nbt = new CompoundNBT();
			stack.setTag(nbt);
		}
		else {
			nbt = stack.getTag();
		}
		CompoundNBT tag = new CompoundNBT();
		
		tag.putInt("MasterX", x);
		tag.putInt("MasterY", y);
		tag.putInt("MasterZ", z);
		nbt.put("MasterPos", tag);
	}
	
	public static void setMasterCoords(ItemStack stack, BlockPos pos) {
		setMasterCoords(stack, pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockPos getMasterCoords(ItemStack stack ) {
		if(!stack.hasTag()) {

			CompoundNBT nbt = new CompoundNBT();
			nbt.put("MasterPos", new CompoundNBT());
			stack.setTag(nbt);
		}
		
		return new BlockPos(getMasterX(stack),getMasterY(stack),getMasterZ(stack));
	}
	
	public static void resetPosition(ItemStack itemStack) {
		CompoundNBT position = new CompoundNBT();

		position.putInt("MasterX", EMPTYSETTING);
		position.putInt("MasterY", EMPTYSETTING);
		position.putInt("MasterZ", EMPTYSETTING);
		position.putInt("dimId", -1);

		itemStack.setTagInfo("MasterPos", position);
	}
	
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity playerIn = context.getPlayer();
		Hand hand = context.getHand();
		
		TileEntity entity = worldIn.getTileEntity(pos);

		ItemStack stack = playerIn.getHeldItem(hand);
		
		if(entity != null) {
			if(entity instanceof ILinkableTile) {
				applySettings(stack, (ILinkableTile)entity, playerIn, worldIn);
				return ActionResultType.SUCCESS;
			}
		}
		else if(playerIn.isSneaking()) {
			resetPosition(stack);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	protected void applySettings(ItemStack itemStack, ILinkableTile pad, PlayerEntity player, World world) {
		if(!isSet(itemStack)) 
			pad.onLinkStart(itemStack, (TileEntity)pad, player, world);
		else
			pad.onLinkComplete(itemStack, (TileEntity)pad, player, world);
	}
}
