package zmaster587.libVulpes.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemProjector extends Item implements IModularInventory, IButtonInventory, INetworkItem, INamedContainerProvider {


	ArrayList<TileMultiBlock> machineList;
	ArrayList<BlockTile> blockList;
	HashMap<Integer,String> descriptionList;

	private static final String IDNAME = "machineId";

	public ItemProjector(Properties properties) {
		super(properties);
		machineList = new ArrayList<>();
		blockList = new ArrayList<>();
		descriptionList = new HashMap<>();
	}

	private String buildMachineBlocks(TileMultiBlock multiblock, BlockTile mainBlock) {
		HashMap<Object, Integer> map = new HashMap<>();

		Object[][][] structure = multiblock.getStructure();

		for (Object[][] objects2d : structure) {
			for (Object[] objects : objects2d) {
				for (Object object : objects) {
					if (!map.containsKey(object)) {
						map.put(object, 1);
					} else
						map.put(object, map.get(object) + 1);
				}
			}
		}

		String str = Item.getItemFromBlock(mainBlock).getDisplayName(new ItemStack(mainBlock)).getString() + " x1\n";

		for(Entry<Object, Integer> entry : map.entrySet()) {

			List<BlockMeta> blockMeta = multiblock.getAllowableBlocks(entry.getKey());

			if(blockMeta.isEmpty() || Item.getItemFromBlock(blockMeta.get(0).getBlock()) == Items.AIR || blockMeta.get(0).getBlock() == Blocks.AIR )
				continue;
			for (BlockMeta meta : blockMeta) {
				String itemStr = Item.getItemFromBlock(meta.getBlock()).getDisplayName(new ItemStack(meta.getBlock(), 1)).getString();
				if (!itemStr.contains("block.")) {
					str = str + itemStr;
					str = str + " or ";
				}
			}

			if(str.endsWith(" or ")) {
				str = str.substring(0, str.length()-4);
			}
			str += " x" + entry.getValue() + "\n";
		}

		return str;
	}

	public void registerMachine(TileMultiBlock multiblock, BlockTile mainBlock) {
		machineList.add(multiblock);
		blockList.add(mainBlock);
	}


	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void mouseEvent(MouseScrollEvent event) {
		if(Minecraft.getInstance().player.isSneaking() && event.getScrollDelta() != 0) {
			ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);

			if(!stack.isEmpty() && stack.getItem() == this && getMachineId(stack) != -1) {
				if(event.getScrollDelta() < 0) {
					setYLevel(stack, getYLevel(stack) + 1);
				}
				else
					setYLevel(stack, getYLevel(stack) - 1);
				event.setCanceled(true);

				PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getInstance().player, (byte)1));
			}
		}
	}

	private void clearStructure(World world, TileMultiBlock tile, @Nonnull ItemStack stack) {
		Direction direction = Direction.values()[getDirection(stack)];

		int prevMachineId = getPrevMachineId(stack);
		Object[][][] structure;
		if(prevMachineId >= 0 && prevMachineId < machineList.size()) {
			structure = machineList.get(prevMachineId).getStructure();

			Vector3F<Integer> basepos = getBasePosition(stack);

			for(int y = 0; y < structure.length; y++) {
				for(int z=0 ; z < structure[0].length; z++) {
					for(int x=0; x < structure[0][0].length; x++) {

						int globalX = basepos.x - x*direction.getZOffset() + z*direction.getXOffset();
						int globalZ = basepos.z + (x* direction.getXOffset()) + (z*direction.getZOffset());
						BlockPos pos = new BlockPos(globalX, basepos.y + y, globalZ);
						if(world.getBlockState(pos).getBlock() == LibVulpesBlocks.blockPhantom)
							world.removeBlock(pos, false);
					}
				}
			}
		}
	}

	private void rebuildStructure(World world, TileMultiBlock tile, ItemStack stack, int posX, int posY, int posZ, Direction orientation, boolean hologram) {
		int id = getMachineId(stack);
		Direction direction;

		TileMultiBlock multiblock = machineList.get(id);
		Object[][][] structure;

		clearStructure(world, tile, stack);

		structure = multiblock.getStructure();
		direction = orientation;

		int y = getYLevel(stack);
		int endNumber, startNumber;

		if(y == -1) {
			startNumber = 0;
			endNumber = structure.length;
		}
		else {
			startNumber = y;
			endNumber = y + 1;
		}
		for(y=startNumber; y < endNumber; y++) {
			for(int z=0 ; z < structure[0].length; z++) {
				for(int x=0; x < structure[0][0].length; x++) {
					List<BlockMeta> block;
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') {
						block = new ArrayList<>();
						block.add(new BlockMeta(blockList.get(id).getDefaultState(), true));
					}
					else if(multiblock.getAllowableBlocks(structure[y][z][x]).isEmpty())
						continue;
					else
						block = multiblock.getAllowableBlocks(structure[y][z][x]);

					int globalX = posX - x*direction.getZOffset() + z*direction.getXOffset();
					int globalZ = posZ + (x* direction.getXOffset())  + (z*direction.getZOffset());
					int globalY = -y + structure.length + posY - 1;
					BlockPos pos = new BlockPos(globalX, globalY, globalZ);

					if((world.isAirBlock(pos) || world.getBlockState(pos).isReplaceable(Fluids.WATER))  &&  block.get(0).getBlock() != Blocks.AIR) {

						if(hologram)
						{
							world.setBlockState(pos, LibVulpesBlocks.blockPhantom.getDefaultState());
							TileEntity newTile = world.getTileEntity(pos);

							//TODO: compatibility fixes with the tile entity not reflecting current block
							if(newTile instanceof TilePlaceholder) {
								((TileSchematic)newTile).setReplacedBlock(block);

								((TilePlaceholder)newTile).setReplacedTileEntity(block.get(0).getBlock().createTileEntity(block.get(0).getBlock().getDefaultState(), world));
							}
						}
						else
						{
							world.setBlockState(pos, block.get(0).getBlockState());
						}
					}
				}
			}
		}
		this.setPrevMachineId(stack, id);
		this.setBasePosition(stack, posX, posY, posZ);
		this.setDirection(stack, orientation.ordinal());
	}

	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(!world.isRemote && player.isSneaking()) {
			INamedContainerProvider stack = (INamedContainerProvider)player.getHeldItem(hand).getItem();
			NetworkHooks.openGui((ServerPlayerEntity)player, stack, packetBuffer -> {packetBuffer.writeInt(getModularInvType().ordinal()); packetBuffer.writeBoolean(hand == Hand.MAIN_HAND);});
			return super.onItemRightClick(world, player, hand);
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();

		int id = getMachineId(stack);

		if(player.abilities.isCreativeMode && !player.isSneaking() && id != -1 && !world.isRemote && blockList.get(id) == context.getWorld().getBlockState(context.getPos()).getBlock()) {

			Vector3F<Integer> pos = getBasePosition(stack);
			Direction direction = Direction.values()[getDirection(stack)];
			setYLevel(stack, -1);
			rebuildStructure(context.getWorld(), machineList.get(id), stack, pos.x, pos.y, pos.z, direction, false);

		}
		if(!player.isSneaking() && id != -1 && world.isRemote) {
			Direction dir = Direction.byIndex(ZUtils.getDirectionFacing(player.rotationYaw - 180));
			TileMultiBlock tile = machineList.get(getMachineId(stack));


			int x = tile.getStructure()[0][0].length;
			int z = tile.getStructure()[0].length;

			int globalX = (-x*dir.getZOffset() + z*dir.getXOffset())/2;
			int globalZ = ((x* dir.getXOffset())  + (z*dir.getZOffset()))/2;

			BlockPos pos = context.getPos();

			TileEntity tile2;
			if((tile2 = world.getTileEntity(pos)) instanceof TileMultiBlock) {
				for(TileMultiBlock tiles:  machineList) {
					if(tile2.getClass() == tiles.getClass()) {

						setMachineId(stack, machineList.indexOf(tiles));
						Object[][][] structure = tiles.getStructure();

						HashedBlockPosition controller = getControllerOffset(structure);
						dir = BlockMultiblockMachine.getFront(world.getBlockState(tile2.getPos())).getOpposite();

						controller.y = (short) (structure.length - controller.y);

						globalX = (-controller.x*dir.getZOffset() + controller.z*dir.getXOffset());
						globalZ = ((controller.x* dir.getXOffset())  + (controller.z*dir.getZOffset()));

						setDirection(stack, dir.ordinal());

						setBasePosition(stack, pos.getX() - globalX, pos.getY() - controller.y  + 1, pos.getZ() - globalZ);
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)0));
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
						return super.onItemUseFirst(stack, context);
					}
				}
			}

			if(context.getFace() == Direction.DOWN)
				setBasePosition(stack, pos.getX() - globalX, pos.getY() - tile.getStructure().length, pos.getZ() - globalZ);
			else
				setBasePosition(stack, pos.getX() - globalX, pos.getY()+1, pos.getZ() - globalZ);
			setDirection(stack, dir.ordinal());

			PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
		}

		return super.onItemUseFirst(stack, context);
	}

	protected HashedBlockPosition getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new HashedBlockPosition(x, y, z);
				}
			}
		}
		return null;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		List<ModuleBase> btns = new LinkedList<>();

		for(int i = 0; 	i <	machineList.size(); i++) {
			TileMultiBlock multiblock = machineList.get(i);
			btns.add(new ModuleButton(60, 4 + i*24, LibVulpes.proxy.getLocalizedString(multiblock.getMachineName()), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
			((ModuleButton)btns.get(i)).setAdditionalData(i);
		}

		ModuleContainerPan panningContainer = new ModuleContainerPan(5, 20, btns, new LinkedList<>(), TextureResources.starryBG, 160, 100, 0, 500);
		modules.add(panningContainer);
		//modules.addAll(btns);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "item.holoProjector";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return entity != null && entity.isAlive() && !entity.getHeldItem(Hand.MAIN_HAND).isEmpty() && entity.getHeldItem(Hand.MAIN_HAND).getItem() == this;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		//PacketHandler.sendToServer(new PacketItemModifcation(this, gui.getInstance().thePlayer, (byte)buttonId));
		ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);
		if(!stack.isEmpty() && stack.getItem() == this) {
			setMachineId(stack, (int)buttonId.getAdditionalData());
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getInstance().player, (byte)0));
		}
	}

	private void setMachineId(ItemStack stack, int id) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		} else
			nbt = new CompoundNBT();

		nbt.putInt(IDNAME, id);
		stack.setTag(nbt);
	}

	private int getMachineId(ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(IDNAME)) {
			return stack.getTag().getInt(IDNAME);
		} else
			return -1;
	}

	private void setYLevel(ItemStack stack, int level) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		} else
			nbt = new CompoundNBT();

		TileMultiBlock machine = machineList.get(getMachineId(stack));

		if(level == -2)
			level = machine.getStructure().length-1;
		else if(level == machine.getStructure().length)
			level = -1;
		nbt.putInt("yOffset", level);
		stack.setTag(nbt);
	}

	private int getYLevel(ItemStack stack) {
		if(stack.hasTag()) {
			return stack.getTag().getInt("yOffset");
		} else
			return -1;
	}

	private void setPrevMachineId(ItemStack stack, int id) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		} else
			nbt = new CompoundNBT();

		nbt.putInt(IDNAME + "Prev", id);
		stack.setTag(nbt);
	}

	private int getPrevMachineId(ItemStack stack) {
		if(stack.hasTag()) {
			return stack.getTag().getInt(IDNAME + "Prev");
		} else
			return -1;
	}

	private Vector3F<Integer> getBasePosition(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			return new Vector3F<>(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
		} else
			return null;
	}

	private void setBasePosition(ItemStack stack, int x, int y, int z) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		} else
			nbt = new CompoundNBT();

		nbt.putInt("x", x);
		nbt.putInt("y", y);
		nbt.putInt("z", z);

		stack.setTag(nbt);
	}

	public int getDirection(ItemStack stack) {
		if(stack.hasTag()) {
			return stack.getTag().getInt("dir");
		} else
			return -1;
	}

	public void setDirection(ItemStack stack, int dir) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		} else
			nbt = new CompoundNBT();

		nbt.putInt("dir", dir);

		stack.setTag(nbt);
	}

	private String getDescription(int id) {
		if(descriptionList.containsKey(id))
			descriptionList.get(id);

		String builtList = buildMachineBlocks(this.machineList.get(id), this.blockList.get(id));
		descriptionList.put(id, builtList);
		return builtList;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag bool) {
		super.addInformation(stack, world, list, bool);

		list.add(new StringTextComponent("Shift right-click: opens machine selection interface"));
		list.add(new StringTextComponent("Shift-scroll: moves cross-section"));

		int id = getMachineId(stack);
		if(id != -1 && machineList != null && machineList.contains(id)) {
			list.add(new StringTextComponent(""));
			list.add(new StringTextComponent(TextFormatting.GREEN + LibVulpes.proxy.getLocalizedString(machineList.get(id).getMachineName())));
			String str = getDescription(id);

			String[] strList = str.split("\n");

			for(String s : strList)
				list.add(new StringTextComponent(s));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack) {
		if(id == 0) {
			out.writeInt(getMachineId(stack));
		}
		else if(id == 1)
			out.writeInt(getYLevel(stack));
		else if(id == 2) {
			Vector3F<Integer> pos = getBasePosition(stack);
			if (pos != null) {
				out.writeInt(pos.x);
				out.writeInt(pos.y);
				out.writeInt(pos.z);
				out.writeInt(getDirection(stack));
			}
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId, CompoundNBT nbt, ItemStack stack) {
		if(packetId == 0) {
			nbt.putInt(IDNAME, in.readInt());
		}
		else if(packetId == 1)
			nbt.putInt("yLevel", in.readInt());
		else if(packetId == 2) {
			nbt.putInt("x", in.readInt());
			nbt.putInt("y", in.readInt());
			nbt.putInt("z", in.readInt());
			nbt.putInt("dir", in.readInt());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt, ItemStack stack) {
		if(id == 0) {
			int machineId = nbt.getInt(IDNAME);
			setMachineId(stack, nbt.getInt(IDNAME));
			TileMultiBlock tile = machineList.get(machineId);
			setYLevel(stack, tile.getStructure().length-1);
		}
		else if(id == 1) {
			setYLevel(stack, nbt.getInt("yLevel"));
			Vector3F<Integer> vec = getBasePosition(stack);
			rebuildStructure(player.world, this.machineList.get(getMachineId(stack)), stack, vec.x, vec.y, vec.z, Direction.byIndex(getDirection(stack)), true);
		}
		else if(id == 2) {
			int x = nbt.getInt("x");
			int y = nbt.getInt("y");
			int z = nbt.getInt("z");
			int dir = nbt.getInt("dir");

			if(getMachineId(stack) != -1)
				rebuildStructure(player.world, this.machineList.get(getMachineId(stack)), stack, x, y, z, Direction.byIndex(dir), true);
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		GuiHandler.guiId ID = getModularInvType();
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_HELD_ITEM, id, player, this.getModules(getModularInvType().ordinal(), player), this, ID);
	}

	@Override
	@Nonnull
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("item.libvulpes.holo_projector");
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULARNOINV;
	}
}
