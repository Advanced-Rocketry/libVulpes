package zmaster587.libVulpes;


import ic2.api.item.IC2Items;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockPhantom;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.block.multiblock.BlockMultiMachineBattery;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketChangeKeyState;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.energy.TileCoalGenerator;
import zmaster587.libVulpes.tile.energy.TilePlugInputIC2;
import zmaster587.libVulpes.tile.energy.TilePlugInputRF;
import zmaster587.libVulpes.tile.energy.TilePlugOutputRF;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.XMLRecipeLoader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="libVulpes",name="Vulpes library",version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@",useMetadata=true, dependencies="before:gregtech;after:CoFHCore;after:BuildCraft|Core")
public class LibVulpes {
	public static Logger logger = Logger.getLogger("libVulpes");
	public static int time = 0;
	private static HashMap<Class, String> userModifiableRecipes = new HashMap<Class, String>();

	@Instance(value = "libVulpes")
	public static LibVulpes instance;

	@SidedProxy(clientSide="zmaster587.libVulpes.client.ClientProxy", serverSide="zmaster587.libVulpes.common.CommonProxy")
	public static CommonProxy proxy;

	private static CreativeTabs tabMultiblock = new CreativeTabs("multiBlock") {
		@Override
		public Item getTabIconItem() {
			return LibVulpesItems.itemLinker;// AdvancedRocketryItems.itemSatelliteIdChip;
		}
	};

	public static CreativeTabs tabLibVulpesOres = new CreativeTabs("advancedRocketryOres") {

		@Override
		public Item getTabIconItem() {
			return MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem();
		}
	};

	public static MaterialRegistry materialRegistry = new MaterialRegistry();

	public static void registerRecipeHandler(Class clazz, String fileName) {
		userModifiableRecipes.put(clazz, fileName);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		//Configuration

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		zmaster587.libVulpes.Configuration.EUMult = (float)config.get(Configuration.CATEGORY_GENERAL, "EUPowerMultiplier", 7, "How many power unit one EU makes").getDouble();

		config.save();

		//
		PacketHandler.addDiscriminator(PacketMachine.class);
		PacketHandler.addDiscriminator(PacketEntity.class);
		PacketHandler.addDiscriminator(PacketChangeKeyState.class);

		//Register Items
		LibVulpesItems.itemLinker = new ItemLinker().setUnlocalizedName("Linker").setCreativeTab(tabMultiblock).setTextureName("libvulpes:Linker");
		LibVulpesItems.itemBattery = new ItemIngredient(2).setUnlocalizedName("libvulpes:battery").setCreativeTab(tabMultiblock);
		LibVulpesItems.itemHoloProjector = new ItemProjector().setUnlocalizedName("holoProjector").setTextureName("libvulpes:holoProjector").setCreativeTab(tabMultiblock);

		GameRegistry.registerItem(LibVulpesItems.itemLinker, "Linker");
		GameRegistry.registerItem(LibVulpesItems.itemBattery, LibVulpesItems.itemBattery.getUnlocalizedName());
		GameRegistry.registerItem(LibVulpesItems.itemHoloProjector, LibVulpesItems.itemHoloProjector.getUnlocalizedName());


		//Register Blocks
		LibVulpesBlocks.blockPhantom = new BlockPhantom(Material.circuits).setBlockName("blockPhantom");
		LibVulpesBlocks.blockHatch = new BlockHatch(Material.rock).setBlockName("hatch").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("libvulpes:machineGeneric").setHardness(1f);
		LibVulpesBlocks.blockAdvStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("advStructureMachine").setBlockTextureName("libvulpes:advStructureBlock").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("structureMachine").setBlockTextureName("libvulpes:structureBlock").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockRFBattery = new BlockMultiMachineBattery(Material.rock, TilePlugInputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rfBattery").setBlockTextureName("libvulpes:batteryRF").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockRFOutput = new BlockMultiMachineBattery(Material.rock, TilePlugOutputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rfOutput").setBlockTextureName("libvulpes:batteryRF").setCreativeTab(tabMultiblock).setHardness(3f);
		
		LibVulpesBlocks.blockCoalGenerator = new BlockTile(TileCoalGenerator.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("coalGenerator").setCreativeTab(tabMultiblock).setHardness(3f);
		((BlockTile)LibVulpesBlocks.blockCoalGenerator).setFrontTexture("furnace_front_off", "furnace_front_on");
		((BlockTile)LibVulpesBlocks.blockCoalGenerator).setSideTexture("furnace_side");
		((BlockTile)LibVulpesBlocks.blockCoalGenerator).setTopTexture("furnace_top");
		
		//Register Blocks
		GameRegistry.registerBlock(LibVulpesBlocks.blockPhantom, LibVulpesBlocks.blockPhantom.getUnlocalizedName());
		GameRegistry.registerBlock(LibVulpesBlocks.blockHatch, ItemBlockMeta.class, "blockHatch");
		GameRegistry.registerBlock(LibVulpesBlocks.blockPlaceHolder, "blockPlaceholder");
		GameRegistry.registerBlock(LibVulpesBlocks.blockStructureBlock, "blockStructureBlock");
		GameRegistry.registerBlock(LibVulpesBlocks.blockRFBattery, "rfBattery");
		GameRegistry.registerBlock(LibVulpesBlocks.blockRFOutput, "batteryOutputRF");
		GameRegistry.registerBlock(LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock.getUnlocalizedName());
		GameRegistry.registerBlock(LibVulpesBlocks.blockCoalGenerator, LibVulpesBlocks.blockCoalGenerator.getUnlocalizedName());
		
		//Register Tile
		GameRegistry.registerTileEntity(TileOutputHatch.class, "ARoutputHatch");
		GameRegistry.registerTileEntity(TileInputHatch.class, "ARinputHatch");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "ARplaceHolder");
		GameRegistry.registerTileEntity(TileFluidHatch.class, "ARFluidHatch");
		GameRegistry.registerTileEntity(TileSchematic.class, "ARTileSchematic");
		GameRegistry.registerTileEntity(TilePlugInputRF.class, "ARrfBattery");
		GameRegistry.registerTileEntity(TilePlugOutputRF.class, "ARrfOutputRF");
		GameRegistry.registerTileEntity(TilePointer.class, "TilePointer");
		GameRegistry.registerTileEntity(TileInventoriedPointer.class, "TileInvPointer");
		GameRegistry.registerTileEntity(TileCoalGenerator.class, "VulpesCoalGenerator");


		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------
		//Items dependant on IC2
		if(Loader.isModLoaded("IC2")) {
			LibVulpesBlocks.blockIC2Plug = new BlockMultiMachineBattery(Material.rock ,TilePlugInputIC2.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("IC2Plug").setBlockTextureName("libvulpes:IC2Plug").setCreativeTab(tabMultiblock).setHardness(3f);
			GameRegistry.registerBlock(LibVulpesBlocks.blockIC2Plug, LibVulpesBlocks.blockIC2Plug.getUnlocalizedName());
			GameRegistry.registerTileEntity(TilePlugInputIC2.class, "ARIC2Plug");
		}

		//Ore dict stuff
		OreDictionary.registerOre("itemBattery", new ItemStack(LibVulpesItems.itemBattery,1,0));

		/*DUST,
		INGOT,
		CRYSTAL,
		BOULE,
		NUGGET,
		COIL(true, AdvancedRocketryBlocks.blockCoil),
		PLATE,
		STICK,
		BLOCK(true, LibVulpesBlocks.blockMetal),
		ORE(true, LibVulpesBlocks.blockOre),
		FAN,
		SHEET,
		GEAR;*/

		//Register allowedProducts
		AllowedProducts.registerProduct("DUST");
		AllowedProducts.registerProduct("INGOT");
		AllowedProducts.registerProduct("CRYSTAL");
		AllowedProducts.registerProduct("BOULE");
		AllowedProducts.registerProduct("NUGGET");
		AllowedProducts.registerProduct("COIL", true);
		AllowedProducts.registerProduct("PLATE");
		AllowedProducts.registerProduct("STICK");
		AllowedProducts.registerProduct("BLOCK", true);
		AllowedProducts.registerProduct("ORE", true);
		AllowedProducts.registerProduct("FAN");
		AllowedProducts.registerProduct("SHEET");
		AllowedProducts.registerProduct("GEAR");
		
		//Register Ores
		
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Dilithium", "pickaxe", 3, 0xddcecb, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("CRYSTAL").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Iron", "pickaxe", 1, 0xafafaf, AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Gold", "pickaxe", 1, 0xffff5d, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Silicon", "pickaxe", 1, 0x2c2c2b, AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("BOULE").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Copper", "pickaxe", 1, 0xd55e28, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Tin", "pickaxe", 1, 0xcdd5d8, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Steel", "pickaxe", 1, 0x55555d, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("FAN").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {"Rutile", "Titanium"}));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Iridium", "pickaxe", 2, 0xdedcce, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue()));
		
		materialRegistry.registerOres(tabLibVulpesOres, "libVulpes");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesItems.itemLinker), "x","y","z", 'x', Items.redstone, 'y', Items.gold_ingot, 'z', Items.iron_ingot);
		FMLCommonHandler.instance().bus().register(this);

		PacketHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		proxy.registerEventHandlers();

		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockIC2Plug), LibVulpesBlocks.blockStructureBlock, IC2Items.getItem("mvTransformer"), LibVulpesItems.itemBattery);
		}
		
		//Recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(LibVulpesBlocks.blockStructureBlock, "sps", "psp", "sps", 'p', "plateIron", 's', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LibVulpesBlocks.blockAdvStructureBlock, "sps", "psp", "sps", 'p', "plateTitanium", 's', "stickTitanium"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(LibVulpesBlocks.blockCoalGenerator, "itemBattery", Blocks.furnace));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//Init TileMultiblock

		//Item output
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 1));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 9));
		TileMultiBlock.addMapping('O', list);

		//Item Inputs
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 0));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 8));
		TileMultiBlock.addMapping('I', list);

		//Power input
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockRFBattery, BlockMeta.WILDCARD));
		if(LibVulpesBlocks.blockIC2Plug != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockIC2Plug, BlockMeta.WILDCARD));
		TileMultiBlock.addMapping('P', list);

		//Power output
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockRFOutput, BlockMeta.WILDCARD));
		TileMultiBlock.addMapping('p', list);

		//Liquid input
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 2));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 10));
		TileMultiBlock.addMapping('L', list);

		//Liquid output
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 3));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 12));
		TileMultiBlock.addMapping('l', list);
	}
	
	//User Recipes
	public void loadXMLRecipe(Class clazz) {
		File file = new File(userModifiableRecipes.get(clazz));
		if(!file.exists()) {
			try {
				file.createNewFile();
				InputStream inputStream = getClass().getResourceAsStream("/assets/libvulpes/defaultrecipe.xml");
				byte fileIO[] = new byte[4096];
				int numRead;
				OutputStream stream;
				if(inputStream != null) {
					stream = new FileOutputStream(file);

					while((numRead = inputStream.read(fileIO)) > 0) {
						stream.write(fileIO, 0, numRead);
					}
					stream.close();
					inputStream.close();
				}
				else {
					BufferedWriter stream2 = new BufferedWriter(new FileWriter(file));
					stream2.write("<Recipes>\n</Recipes>");
					stream2.close();
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			XMLRecipeLoader loader = new XMLRecipeLoader();
			try {
				loader.loadFile(file);
				loader.registerRecipes(clazz);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Mod.EventHandler
	public void missingMappingEvent(FMLMissingMappingsEvent event) {
		Iterator<MissingMapping> itr = event.getAll().iterator();
		while(itr.hasNext()) {
			MissingMapping mapping = itr.next();

			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.battery"))
				mapping.remap(LibVulpesItems.itemBattery);

			if(mapping.name.equalsIgnoreCase("advancedrocketry:" + LibVulpesBlocks.blockPhantom.getUnlocalizedName())) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockPhantom);
				}
				else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockPhantom));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:" + LibVulpesItems.itemHoloProjector.getUnlocalizedName())) {
				mapping.remap(LibVulpesItems.itemHoloProjector);
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:blockHatch")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockHatch);
				} else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockHatch));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:blockPlaceholder")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockPlaceHolder);
				} else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockPlaceHolder));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:blockStructureBlock")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockStructureBlock);
				} else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockStructureBlock));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:rfBattery")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockRFBattery);
				} else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockRFBattery));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:batteryOutputRF")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(LibVulpesBlocks.blockRFOutput);
				} else
					mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockRFOutput));
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:tile.IC2Plug")) {
				if(LibVulpesBlocks.blockIC2Plug != null) {
					if(mapping.type == mapping.type.BLOCK) {
						mapping.remap(LibVulpesBlocks.blockIC2Plug);
					} else
						mapping.remap(Item.getItemFromBlock(LibVulpesBlocks.blockIC2Plug));
				}
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:metal0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("BLOCK")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("BLOCK")).getItem());
			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:coil0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("COIL")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("COIL")).getItem());

			}

			if(mapping.name.equalsIgnoreCase("advancedrocketry:ore0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productingot")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("INGOT")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productboule")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Silicon").getProduct(AllowedProducts.getProductByName("BOULE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productgear")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("GEAR")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productplate")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("PLATE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productdust")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("DUST")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productrod")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("STICK")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productfan")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Steel").getProduct(AllowedProducts.getProductByName("FAN")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productcrystal")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("CRYSTAL")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productnugget")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("NUGGET")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("advancedrocketry:productsheet")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("SHEET")).getItem());
			}
			
			
			
			
			if(mapping.name.equalsIgnoreCase("libVulpes:metal0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("BLOCK")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("BLOCK")).getItem());
			}

			if(mapping.name.equalsIgnoreCase("libVulpes:coil0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("COIL")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("COIL")).getItem());

			}

			if(mapping.name.equalsIgnoreCase("libVulpes:ore0")) {
				if(mapping.type == mapping.type.BLOCK) {
					mapping.remap(Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem()));
				} else
					mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productingot")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("INGOT")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productboule")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Silicon").getProduct(AllowedProducts.getProductByName("BOULE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productgear")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("GEAR")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productplate")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("PLATE")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productdust")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("DUST")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productrod")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("STICK")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productfan")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Steel").getProduct(AllowedProducts.getProductByName("FAN")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productcrystal")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("CRYSTAL")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productnugget")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("NUGGET")).getItem());
			}
			
			if(mapping.name.equalsIgnoreCase("libVulpes:productsheet")) {
				mapping.remap(MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("SHEET")).getItem());
			}
		}
	}



	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		time++;
	}
}

