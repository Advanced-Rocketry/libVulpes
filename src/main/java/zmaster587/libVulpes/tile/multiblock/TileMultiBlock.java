package zmaster587.libVulpes.tile.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileMultiBlock extends TileEntity {

	/*CanRender must be seperate from incomplete because some multiblocks must be completed on the client but
	because chunks on the client.  It is also used to determine if the block on the server has ever been complete */
	protected boolean completeStructure, canRender;
	protected byte timeAlive = 0;

	protected LinkedList<IInventory> itemInPorts = new LinkedList<IInventory>();
	protected LinkedList<IInventory> itemOutPorts = new LinkedList<IInventory>();

	protected LinkedList<IFluidHandler> fluidInPorts = new LinkedList<IFluidHandler>();
	protected LinkedList<IFluidHandler> fluidOutPorts = new LinkedList<IFluidHandler>();

	protected static HashMap<Character, List<BlockMeta>> charMapping = new HashMap<Character, List<BlockMeta>>();

	public TileMultiBlock() {
		completeStructure = false;
		canRender = false;
	}


	public static void addMapping(char character, List<BlockMeta> listToAdd) {
		if(charMapping.containsKey(character))
			LibVulpes.logger.warning("Overwritting Multiblock mapping of \"" + character + "\"");
		charMapping.put(character, listToAdd);
	}

	public static List<BlockMeta> getMapping(char character) {
		return charMapping.get(character);
	}

	/**
	 * Note: it may be true on the server but not the client.  This is because the client needs to form the multiblock
	 * so the tile has references to other blocks in its structure for gui display etc
	 * @return true if the structure is complete
	 */
	public boolean isComplete() {
		return completeStructure;
	}

	/**
	 * 
	 * @return true if the block should be rendered as complete
	 */
	@SideOnly(Side.CLIENT)
	public boolean canRender() {
		return canRender;
	}

	/**
	 * @return the unlocalized name of the machine
	 */
	public String getMachineName() {
		return "";
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistance(xCoord, yCoord, zCoord) < 64;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeNetworkData(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}

	public void invalidateComponent(TileEntity tile) {
		setComplete(false);
	}

	/**Called by inventory blocks that are part of the structure
	 ** This includes recipe management etc
	 **/
	public void onInventoryUpdated() {
	}

	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, int destroyedX, int destroyedY, int destroyedZ, boolean blockBroken) {
		canRender = completeStructure = false;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() & 7, 2); //Turn off machine

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		//UNDO all the placeholder blocks
		ForgeDirection front = getFrontDirection();

		Object[][][] structure = getStructure();
		Vector3F<Integer> offset = getControllerOffset(structure);


		//Mostly to make sure IMultiblocks lose their choke-hold on this machines and to revert placeholder blocks
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;


					//This block is being broken anyway so don't bother
					if(blockBroken && globalX == destroyedX &&
							globalY == destroyedY &&
							globalZ == destroyedZ)
						continue;
					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);

					destroyBlockAt(globalX, globalY, globalZ, block, tile);

				}
			}
		}

		resetCache();
	}

	/**
	 * Called when the multiblock is being deconstructed.  This is called for each block in the structure.
	 * Provided in case of special handling
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param tile
	 */
	protected void destroyBlockAt(int x, int y, int z, Block block, TileEntity tile) {

		//if it's an instance of multiblock structure call its destroyStructure method
		if(block instanceof BlockMultiblockStructure) {
			((BlockMultiblockStructure)block).destroyStructure(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z));
		}

		//If the the tile is a placeholder then make sure to replace it with its original block and tile
		if(tile instanceof TilePlaceholder && !(tile instanceof TileSchematic)) {
			TilePlaceholder placeholder = (TilePlaceholder)tile;

			//Must set incomplete BEFORE changing the block to prevent stack overflow!
			placeholder.setIncomplete();

			worldObj.setBlock(tile.xCoord, tile.yCoord, tile.zCoord, placeholder.getReplacedBlock(), placeholder.getReplacedBlockMeta(), 3);

			//Dont try to set a tile if none existed
			if(placeholder.getReplacedTileEntity() != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				placeholder.getReplacedTileEntity().writeToNBT(nbt);

				worldObj.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord).readFromNBT(nbt);
			}
		}
		//Make all pointers incomplete
		else if(tile instanceof IMultiblock) {
			((IMultiblock)tile).setIncomplete();
		}
	}

	public ForgeDirection getFrontDirection() {
		return RotatableBlock.getFront(this.getBlockMetadata());
	}

	public Object[][][] getStructure() {
		return null;
	}

	public boolean attemptCompleteStructure() {
		//if(!completeStructure)
		canRender = completeStructure = completeStructure();
		return completeStructure;
	}

	public void setComplete(boolean complete) {
		completeStructure = complete;
	}

	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list =new ArrayList<BlockMeta>();
		return list;
	}

	/**
	 * Called when cached Tiles need to be cleared (batteries/IO/etc)
	 */
	public void resetCache() {
		itemInPorts.clear();
		itemOutPorts.clear();
		fluidInPorts.clear();
		fluidOutPorts.clear();
	}


	/**
	 * Use '*' to allow any kind of Hatch, or energy device or anything returned by getAllowableWildcards
	 * Use 'L' for liquid input hatches
	 * Use 'l' for liquid output hatches
	 * Use 'I' for input hatch
	 * Use 'O' for output hatch
	 * Use 'P' for power input
	 * Use 'p' for power output
	 * Use 'D' for data hatch
	 * Use 'c' for the main Block, there can only be one
	 * Use null for anything
	 * Use a Block to force the user to place that block there
	 * @return true if the structure is valid
	 */
	protected boolean completeStructure() {

		//Make sure the environment is clean
		resetCache();

		Object[][][] structure = getStructure();

		Vector3F<Integer> offset = getControllerOffset(structure);
		List<BlockPosition> replacableBlocks = new LinkedList<BlockPosition>();

		ForgeDirection front = getFrontDirection();

		//Store tile entities for later processing so we don't risk the check failing halfway through leaving half the multiblock assigned
		LinkedList<TileEntity> tiles = new LinkedList<TileEntity>();

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					if(structure[y][z][x] == null)
						continue;

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;

					if(!worldObj.getChunkFromBlockCoords(globalX, globalZ).isChunkLoaded)
						return false;

					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);
					int meta = worldObj.getBlockMetadata(globalX, globalY, globalZ);

					if(block == LibVulpesBlocks.blockPhantom)
						return false;

					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePointer) {
						TileEntity masterBlock;
						
						if(((IMultiblock)tile).hasMaster() && (masterBlock = ((IMultiblock)tile).getMasterBlock()) != this) {
							
							//This ~should~ only occur with world edits and being moved to space stations and such
							if(masterBlock == null)
								((IMultiblock)tile).setMasterBlock(this.xCoord, this.yCoord, this.zCoord);
							
							if(((IMultiblock)tile).getMasterBlock().xCoord == this.xCoord && ((IMultiblock)tile).getMasterBlock().yCoord == this.yCoord
									&& ((IMultiblock)tile).getMasterBlock().zCoord == this.zCoord) {
								continue;
							}
						}
						else if(((IMultiblock)tile).getMasterBlock() == this) 
							continue;
					}
					//Make sure the structure is valid
					if(!(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') && !(structure[y][z][x] instanceof Block && (Block)structure[y][z][x] == Blocks.air && worldObj.isAirBlock(globalX, globalY, globalZ)) && !getAllowableBlocks(structure[y][z][x]).contains(new BlockMeta(block,meta))) {

						//Can it be replaced?
						if(block.isReplaceable(worldObj, globalX, globalY, globalZ) && structure[y][z][x] instanceof Block && (Block)structure[y][z][x] == Blocks.air )
							replacableBlocks.add(new BlockPosition(globalX, globalY, globalZ));
						else {
							LibVulpes.proxy.spawnParticle("errorBox", worldObj, globalX, globalY, globalZ, 0, 0, 0);
							return false;
						}
						LibVulpes.proxy.spawnParticle("errorBox", worldObj, globalX, globalY, globalZ, 0, 0, 0);
						return false;
					}
				}
			}
		}

		//Notify all blocks in the structure that it's being build and assimilate them
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;


					Block block = worldObj.getBlock(globalX, globalY, globalZ);
					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);

					if(block instanceof BlockMultiBlockComponentVisible) {
						((BlockMultiBlockComponentVisible)block).hideBlock(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));

						tile = worldObj.getTileEntity(globalX, globalY, globalZ);

						if(tile instanceof IMultiblock)
							((IMultiblock)tile).setComplete(this.xCoord, this.yCoord, this.zCoord);
					}
					else if(block instanceof BlockMultiblockStructure) {
						if(shouldHideBlock(worldObj, globalX, globalY, globalZ, block))
							((BlockMultiblockStructure)block).hideBlock(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(structure[y][z][x] != null && !block.isAir(worldObj, globalX, globalY, globalZ) && !(tile instanceof IMultiblock) && !(tile instanceof TileMultiBlock)) {
						replaceStandardBlock(globalX,globalY, globalZ, block, worldObj.getBlockMetadata(globalX, globalY, globalZ), tile);
					}
				}
			}
		}

		//Now that we know the multiblock is valid we can assign
		for(TileEntity tile : tiles) {
			integrateTile(tile);
		}
		
		//Replace and drop replaceable blocks
		for(BlockPosition pos : replacableBlocks) {
			worldObj.setBlockToAir(pos.x, pos.y, pos.z);
		}
		
		
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return true;
	}


	public List<BlockMeta> getAllowableBlocks(Object input) {
		if(input instanceof Character && (Character)input == '*') {
			return getAllowableWildCardBlocks();
		}
		else if(input instanceof Character  && charMapping.containsKey((Character)input)) {
			return charMapping.get((Character)input);
		}
		else if(input instanceof String) { //OreDict entry
			List<ItemStack> stacks = OreDictionary.getOres((String)input);
			List<BlockMeta> list = new LinkedList<BlockMeta>();
			for(ItemStack stack : stacks) {
				//stack.get
				Block block = Block.getBlockFromItem(stack.getItem());
				if(block != null && block != Blocks.air)
					list.add(new BlockMeta(block, stack.getItem().getMetadata(stack.getItemDamage())));
			}
			return list;
		}
		else if(input instanceof Block) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add(new BlockMeta((Block) input, BlockMeta.WILDCARD));
			return list;
		}
		else if(input instanceof BlockMeta) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add((BlockMeta) input);
			return list;
		}
		else if(input instanceof Block[]) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			for(Block b : (Block[])input) list.add(new BlockMeta(b));
			return list;
		}
		else if(input instanceof List) {
			return (List<BlockMeta>)input;
		}
		List<BlockMeta> list = new ArrayList<BlockMeta>();
		return list;
	}

	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		return false;
	}

	/**
	 * Called when replacing a block that is not specifically designed to be compatible with the multiblocks.  Eg iron black
	 * Most multiblocks have a renderer and so these blocks are converted to an invisible pointer
	 * @return
	 */
	protected void replaceStandardBlock(int xCoord, int yCoord, int zCoord, Block block, int meta, TileEntity tile) {

		worldObj.setBlock(xCoord, yCoord, zCoord, LibVulpesBlocks.blockPlaceHolder);
		TilePlaceholder newTile = (TilePlaceholder)worldObj.getTileEntity(xCoord, yCoord, zCoord);

		newTile.setReplacedBlock(block);
		newTile.setReplacedBlockMeta((byte)meta);
		newTile.setReplacedTileEntity(tile);
		newTile.setMasterBlock(this.xCoord, this.yCoord, this.zCoord);
	}

	/**
	 * This is used so classes extending this one can have their own handling of tiles without overriding the method
	 * @param tile Current tile in multiblock
	 */
	protected void integrateTile(TileEntity tile) {
		if(tile instanceof IMultiblock)
			((IMultiblock) tile).setComplete(xCoord, yCoord, zCoord);

		if(tile instanceof TileInputHatch)
			itemInPorts.add((IInventory) tile);
		else if(tile instanceof TileOutputHatch) 
			itemOutPorts.add((IInventory) tile);
		else if(tile instanceof TileFluidHatch) {
			TileFluidHatch liquidHatch = (TileFluidHatch)tile;
			if(liquidHatch.isOutputOnly())
				fluidOutPorts.add((IFluidHandler)liquidHatch);
			else
				fluidInPorts.add((IFluidHandler)liquidHatch);
		}
	}

	protected Vector3F<Integer> getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new Vector3F<Integer>(x, y, z);
				}
			}
		}
		return null;
	}

	protected void writeNetworkData(NBTTagCompound nbt) {

	}

	protected void readNetworkData(NBTTagCompound nbt) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNetworkData(nbt);
		nbt.setBoolean("completeStructure", completeStructure);
		nbt.setBoolean("canRender", canRender);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNetworkData(nbt);
		completeStructure = nbt.getBoolean("completeStructure");
		canRender = nbt.getBoolean("canRender");
	}
}
