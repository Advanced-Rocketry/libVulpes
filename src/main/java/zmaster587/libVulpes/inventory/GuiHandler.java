package zmaster587.libVulpes.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.DistExecutor;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;

public class GuiHandler {

	public enum guiId {
		MODULAR,
		MODULARNOINV,
		MODULARCENTEREDFULLSCREEN,
		MODULARFULLSCREEN
	}
	
	public static Entity getEntityFromBuf(PacketBuffer buf) {
		return DistExecutor.runForDist(() -> () -> {
			int id = buf.readInt();
			return Minecraft.getInstance().world.getEntityByID(id);
		}, () -> () -> {
			throw new RuntimeException("This should not be called on a server!");
		});
	}

	
	public static TileEntity getTeFromBuf(PacketBuffer buf) {
		return DistExecutor.runForDist(() -> () -> {
			BlockPos pos = buf.readBlockPos();
			return Minecraft.getInstance().player.world.getTileEntity(pos);
		}, () -> () -> {
			throw new RuntimeException("This should not be called on a server!");
		});
	}

	public static ItemStack getHeldFromBuf(PacketBuffer buf) {
		return DistExecutor.runForDist(() -> () -> {
			Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
			return Minecraft.getInstance().player.getHeldItem(hand);
		}, () -> () -> {
			throw new RuntimeException("This should not be called on a server!");
		});
	}
	
	public static boolean doesIncludePlayerInv(int ID)
	{
		return ID == guiId.MODULAR.ordinal();
	}
	
	public static boolean doesIncludeHotBar(int ID)
	{
		return ID != guiId.MODULARFULLSCREEN.ordinal() && ID != guiId.MODULARCENTEREDFULLSCREEN.ordinal();
	}
	
	/*//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

		Object tile;
		BlockPos pos = new BlockPos(x,y,z);

		if(y > -1)
			tile = world.getTileEntity(pos);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.MODULAR || ID == guiId.MODULARNOINV || ID == guiId.MODULARFULLSCREEN || ID == guiId.MODULARCENTEREDFULLSCREEN.ordinal()) {
			return new ContainerModular(player, ((IModularInventory)tile).getModules(ID, player), ((IModularInventory)tile), ID == guiId.MODULAR, ID != guiId.MODULARFULLSCREEN && ID != guiId.MODULARCENTEREDFULLSCREEN.ordinal());
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

		Object tile;
		BlockPos pos = new BlockPos(x,y,z);
		
		if(y > -1)
			tile = world.getTileEntity(pos);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.MODULAR || ID == guiId.MODULARNOINV || ID == guiId.MODULARCENTEREDFULLSCREEN.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModular(player, modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR, ID != guiId.MODULARCENTEREDFULLSCREEN.ordinal(), modularTile.getModularInventoryName());
		}
		else if(ID == guiId.MODULARFULLSCREEN) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModularFullScreen(player,modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR, false, modularTile.getModularInventoryName());
		}
		return null;
	}*/

}
