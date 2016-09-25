package zmaster587.libVulpes;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockPhantom;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.block.multiblock.BlockMultiMachineBattery;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.items.ItemOreProduct;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketChangeKeyState;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.energy.TilePowerInput;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;

@Mod(modid="libVulpes",name="Vulpes library",version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@",useMetadata=true, dependencies="before:gregtech;after:CoFHCore;after:BuildCraft|Core")
public class LibVulpes {
	public static Logger logger;
	public static int time = 0;

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
		LibVulpesItems.itemLinker = new ItemLinker().setUnlocalizedName("Linker").setCreativeTab(tabMultiblock);
		LibVulpesItems.itemBattery = new ItemIngredient(2).setUnlocalizedName("libvulpes:battery").setCreativeTab(tabMultiblock);
		LibVulpesItems.itemHoloProjector = new ItemProjector().setUnlocalizedName("holoProjector").setCreativeTab(tabMultiblock);

		LibVulpesBlocks.registerItem(LibVulpesItems.itemLinker.setRegistryName("linker"));
		LibVulpesBlocks.registerItem(LibVulpesItems.itemBattery.setRegistryName("battery"));
		LibVulpesBlocks.registerItem(LibVulpesItems.itemHoloProjector.setRegistryName(LibVulpesItems.itemHoloProjector.getUnlocalizedName().substring(5)));


		//Register Blocks
		LibVulpesBlocks.blockPhantom = new BlockPhantom(Material.CIRCUITS).setUnlocalizedName("blockPhantom");
		LibVulpesBlocks.blockHatch = new BlockHatch(Material.ROCK).setUnlocalizedName("hatch").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setUnlocalizedName("placeHolder").setHardness(1f);
		LibVulpesBlocks.blockAdvStructureBlock = new BlockAlphaTexture(Material.ROCK).setUnlocalizedName("advStructureMachine").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockStructureBlock = new BlockAlphaTexture(Material.ROCK).setUnlocalizedName("structureMachine").setCreativeTab(tabMultiblock).setHardness(3f);
		LibVulpesBlocks.blockInputPlug = new BlockMultiMachineBattery(Material.ROCK, TilePowerInput.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("powerBattery").setCreativeTab(tabMultiblock).setHardness(3f);
		//LibVulpesBlocks.blockRFBattery = new BlockMultiMachineBattery(Material.ROCK, TilePlugInputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("rfBattery").setCreativeTab(tabMultiblock).setHardness(3f);
		//LibVulpesBlocks.blockRFOutput = new BlockMultiMachineBattery(Material.ROCK, TilePlugOutputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("rfOutput").setCreativeTab(tabMultiblock).setHardness(3f);


		//Register Blocks
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockPhantom.setRegistryName(LibVulpesBlocks.blockPhantom.getUnlocalizedName().substring(5)));
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockHatch.setRegistryName(LibVulpesBlocks.blockHatch.getUnlocalizedName().substring(5)), ItemBlockMeta.class, false);
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockPlaceHolder.setRegistryName(LibVulpesBlocks.blockPlaceHolder.getUnlocalizedName().substring(5)));
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockStructureBlock.setRegistryName(LibVulpesBlocks.blockStructureBlock.getUnlocalizedName().substring(5)));
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockInputPlug.setRegistryName(LibVulpesBlocks.blockInputPlug.getUnlocalizedName().substring(5)));
		//LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockRFBattery.setRegistryName(LibVulpesBlocks.blockRFBattery.getUnlocalizedName()));
		//LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockRFOutput.setRegistryName(LibVulpesBlocks.blockRFOutput.getUnlocalizedName()));
		LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockAdvStructureBlock.setRegistryName(LibVulpesBlocks.blockAdvStructureBlock.getUnlocalizedName().substring(5)));

		//Register Tile
		GameRegistry.registerTileEntity(TileOutputHatch.class, "ARoutputHatch");
		GameRegistry.registerTileEntity(TileInputHatch.class, "ARinputHatch");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "ARplaceHolder");
		GameRegistry.registerTileEntity(TileFluidHatch.class, "ARFluidHatch");
		GameRegistry.registerTileEntity(TileSchematic.class, "ARTileSchematic");
		GameRegistry.registerTileEntity(TilePowerInput.class, "ARBattery");
		//GameRegistry.registerTileEntity(TilePlugInputRF.class, "ARrfBattery");
		//GameRegistry.registerTileEntity(TilePlugOutputRF.class, "ARrfOutputRF");
		GameRegistry.registerTileEntity(TilePointer.class, "TilePointer");
		GameRegistry.registerTileEntity(TileInventoriedPointer.class, "TileInvPointer");


		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------
		//Items dependant on IC2
		if(Loader.isModLoaded("IC2")) {
			//LibVulpesBlocks.blockIC2Plug = new BlockMultiMachineBattery(Material.rock ,TilePlugInputIC2.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("IC2Plug").setBlockTextureName("libvulpes:IC2Plug").setCreativeTab(tabMultiblock).setHardness(3f);
			//GameRegistry.registerBlock(LibVulpesBlocks.blockIC2Plug, LibVulpesBlocks.blockIC2Plug.getUnlocalizedName());
			//GameRegistry.registerTileEntity(TilePlugInputIC2.class, "ARIC2Plug");
		}




		if(FMLCommonHandler.instance().getSide().isClient()) {
			//Register Block models
			Item blockItem = Item.getItemFromBlock(LibVulpesBlocks.blockHatch);
			ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("libvulpes:inputHatch", "inventory"));
			ModelLoader.setCustomModelResourceLocation(blockItem, 1, new ModelResourceLocation("libvulpes:outputHatch", "inventory"));
			ModelLoader.setCustomModelResourceLocation(blockItem, 2, new ModelResourceLocation("libvulpes:fluidInputHatch", "inventory"));
			ModelLoader.setCustomModelResourceLocation(blockItem, 3, new ModelResourceLocation("libvulpes:fluidOutputHatch", "inventory"));

			//Register Item models
			ModelLoader.setCustomModelResourceLocation(LibVulpesItems.itemLinker, 0, new ModelResourceLocation(LibVulpesItems.itemLinker.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(LibVulpesItems.itemHoloProjector, 0, new ModelResourceLocation(LibVulpesItems.itemHoloProjector.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(LibVulpesItems.itemBattery, 0, new ModelResourceLocation("libvulpes:smallBattery", "inventory"));
			ModelLoader.setCustomModelResourceLocation(LibVulpesItems.itemBattery, 1, new ModelResourceLocation("libvulpes:small2xBattery", "inventory"));
		}

		//Ore dict stuff
		OreDictionary.registerOre("battery", new ItemStack(LibVulpesItems.itemBattery,1,0));

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
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Copper", "pickaxe", 1, 0xd55e28, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Tin", "pickaxe", 1, 0xcdd5d8, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Steel", "pickaxe", 1, 0x55555d, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("FAN").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {"Rutile", "Titanium"}));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("Aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));

		materialRegistry.registerOres(tabLibVulpesOres);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesItems.itemLinker), "x","y","z", 'x', Items.REDSTONE, 'y', Items.GOLD_INGOT, 'z', Items.IRON_INGOT);
		MinecraftForge.EVENT_BUS.register(this);

		PacketHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		proxy.registerEventHandlers();

		if(Loader.isModLoaded("IC2")) {
			//GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockIC2Plug), LibVulpesBlocks.blockStructureBlock, IC2Items.getItem("mvTransformer"), LibVulpesItems.itemBattery);
		}

		//Recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(LibVulpesBlocks.blockStructureBlock, "sps", "psp", "sps", 'p', "plateIron", 's', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LibVulpesBlocks.blockAdvStructureBlock, "sps", "psp", "sps", 'p', "plateTitanium", 's', "stickTitanium"));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		materialRegistry.postInit();

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
		list.add(new BlockMeta(LibVulpesBlocks.blockInputPlug, BlockMeta.WILDCARD));
		if(LibVulpesBlocks.blockRFBattery != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockRFBattery, BlockMeta.WILDCARD));
		if(LibVulpesBlocks.blockIC2Plug != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockIC2Plug, BlockMeta.WILDCARD));
		TileMultiBlock.addMapping('P', list);

		//Power output
		list = new LinkedList<BlockMeta>();
		if(LibVulpesBlocks.blockRFOutput != null)
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


	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		time++;
	}
}

