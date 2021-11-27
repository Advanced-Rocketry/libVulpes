package zmaster587.libVulpes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.*;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.block.multiblock.BlockMultiMachineBattery;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.libVulpes.client.ClientProxy;
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.config.LibVulpesConfig;
import zmaster587.libVulpes.event.BucketHandler;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.tile.energy.TileCreativePowerInput;
import zmaster587.libVulpes.tile.energy.TileForgePowerInput;
import zmaster587.libVulpes.tile.energy.TileForgePowerOutput;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.OreGen;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

// name="Vulpes library",version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@",useMetadata=true, dependencies="after:ic2;after:cofhcore;after:buildcraft|core"
@Mod(LibVulpes.MODID)
public class LibVulpes {
	public static org.apache.logging.log4j.Logger logger = LogManager.getLogger("libVulpes");
	public static int time = 0;

	public final static String MODID = "libvulpes";
	public static LibVulpes instance;
	public static ModContainer MOD_CONTAINER;
	//Classload

	public static CommonProxy proxy = (CommonProxy) DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	private static ItemGroup tabMultiblock = new ItemGroup("multiBlock") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(LibVulpesItems.itemLinker);
		}
	};

	public static ItemGroup tabLibVulpesOres = new ItemGroup("libvulpesmaterials") {

		@Override
		@Nonnull
		public ItemStack createIcon() {
			return MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE"));
		}
	};

	public static MaterialRegistry materialRegistry = new MaterialRegistry();

	public LibVulpes() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

		//Initialze Blocks
		LibVulpesBlocks.blockPhantom = new BlockPhantom(net.minecraft.block.AbstractBlock.Properties.create(Material.MISCELLANEOUS)).setRegistryName("block_phantom");

		LibVulpesBlocks.blockItemOutputHatch = new BlockHatch(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("itemohatch");
		LibVulpesBlocks.blockFluidOutputHatch = new BlockHatch(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("fluidohatch");
		LibVulpesBlocks.blockItemInputHatch = new BlockHatch(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("itemihatch");
		LibVulpesBlocks.blockFluidInputHatch = new BlockHatch(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("fluidihatch");
		LibVulpesBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setRegistryName("place_holder");
		LibVulpesBlocks.blockAdvancedMachineStructure = new Block(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("advancedmachinestructure");
		LibVulpesBlocks.blockMachineStructure = new Block(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)).setRegistryName("machinestructure");
		LibVulpesBlocks.blockCreativeInputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3), TileCreativePowerInput.class, GuiHandler.guiId.MODULAR).setRegistryName("creative_power_battery");
		LibVulpesBlocks.blockForgeInputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f), TileForgePowerInput.class, GuiHandler.guiId.MODULAR).setRegistryName("forge_power_input");
		LibVulpesBlocks.blockForgeOutputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f), TileForgePowerOutput.class, GuiHandler.guiId.MODULAR).setRegistryName("forge_power_output");
		LibVulpesBlocks.blockCoalGenerator = new BlockTile(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f), GuiHandler.guiId.MODULAR).setRegistryName("coal_generator");

		net.minecraft.block.AbstractBlock.Properties motorProperties = net.minecraft.block.AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2).notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false).setBlocksVision((p_test_1_, p_test_2_, p_test_3_) -> false);
		LibVulpesBlocks.blockMotor = new BlockMotor(motorProperties, 1f).setRegistryName("motor");
		LibVulpesBlocks.blockAdvancedMotor = new BlockMotor(motorProperties, 1/1.5f).setRegistryName("advanced_motor");
		LibVulpesBlocks.blockEnhancedMotor = new BlockMotor(motorProperties, 1/2f).setRegistryName("enhanced_motor");
		LibVulpesBlocks.blockEliteMotor = new BlockMotor(motorProperties, 1/4f).setRegistryName("elite_motor");

		//Initialize Items
		LibVulpesItems.itemLinker = new ItemLinker(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("linker");
		LibVulpesItems.itemBattery = new Item(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("battery");
		LibVulpesItems.itemBatteryPack = new Item(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("batterypack");
		LibVulpesItems.itemHoloProjector = new ItemProjector(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("holo_projector");


		LibVulpesItems.itemItemOutputHatch = new BlockItem(LibVulpesBlocks.blockItemOutputHatch, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("itemohatch");
		LibVulpesItems.itemFluidOutputHatch = new BlockItem(LibVulpesBlocks.blockFluidOutputHatch, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("fluidohatch");
		LibVulpesItems.itemItemInputHatch = new BlockItem(LibVulpesBlocks.blockItemInputHatch, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("itemihatch");
		LibVulpesItems.itemFluidInputHatch = new BlockItem(LibVulpesBlocks.blockFluidInputHatch, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("fluidihatch");
		LibVulpesItems.itemAdvancedMachineStructure = new BlockItem(LibVulpesBlocks.blockAdvancedMachineStructure, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("advancedmachinestructure");
		LibVulpesItems.itemMachineStructure = new BlockItem(LibVulpesBlocks.blockMachineStructure, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("machinestructure");
		LibVulpesItems.itemCreativeInputPlug = new BlockItem(LibVulpesBlocks.blockCreativeInputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("creative_power_battery");
		LibVulpesItems.itemForgeInputPlug = new BlockItem(LibVulpesBlocks.blockForgeInputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("forge_power_input"); 
		LibVulpesItems.itemForgeOutputPlug = new BlockItem(LibVulpesBlocks.blockForgeOutputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("forge_power_output");
		LibVulpesItems.itemCoalGenerator = new BlockItem(LibVulpesBlocks.blockCoalGenerator, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("coal_generator");
		LibVulpesItems.itemMotor = new BlockItem(LibVulpesBlocks.blockMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("motor");
		LibVulpesItems.itemAdvancedMotor = new BlockItem(LibVulpesBlocks.blockAdvancedMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("advanced_motor");
		LibVulpesItems.itemEnhancedMotor = new BlockItem(LibVulpesBlocks.blockEnhancedMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("enhanced_motor");
		LibVulpesItems.itemEliteMotor = new BlockItem(LibVulpesBlocks.blockEliteMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("elite_motor");



		//Configuration
		LibVulpesConfig.register();

		//Register allowed products
		AllowedProducts.registerProduct("DUST");
		AllowedProducts.registerProduct("INGOT");
		AllowedProducts.registerProduct("GEM");
		AllowedProducts.registerProduct("BOULE");
		AllowedProducts.registerProduct("NUGGET");
		AllowedProducts.registerProduct("COIL", true);
		AllowedProducts.registerProduct("PLATE");
		AllowedProducts.registerProduct("ROD");
		AllowedProducts.registerProduct("BLOCK", true);
		AllowedProducts.registerProduct("ORE", true);
		AllowedProducts.registerProduct("FAN");
		AllowedProducts.registerProduct("SHEET");
		AllowedProducts.registerProduct("GEAR");

		//Register materials
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("dilithium", "pickaxe", 3, 0xddcecb, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("GEM").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("iron", "pickaxe", 1, 0xafafaf, AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("gold", "pickaxe", 1, 0xffff5d, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("silicon", "pickaxe", 1, 0x2c2c2b, AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("BOULE").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("copper", "pickaxe", 1, 0xd55e28, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("steel", "pickaxe", 1, 0x55555d, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("FAN").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {"rutile", "titanium"}));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("iridium", "pickaxe", 2, 0xdedcce, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue()));
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerItems(RegistryEvent.Register<Item> evt) {
		//Register Items

		evt.getRegistry().registerAll(LibVulpesItems.itemLinker,
				LibVulpesItems.itemBattery,
				LibVulpesItems.itemBatteryPack,
				LibVulpesItems.itemHoloProjector,
				LibVulpesItems.itemItemOutputHatch,
				LibVulpesItems.itemFluidOutputHatch,
				LibVulpesItems.itemItemInputHatch,
				LibVulpesItems.itemFluidInputHatch,
				LibVulpesItems.itemAdvancedMachineStructure,
				LibVulpesItems.itemMachineStructure,
				LibVulpesItems.itemCreativeInputPlug,
				LibVulpesItems.itemForgeInputPlug,
				LibVulpesItems.itemForgeOutputPlug,
				LibVulpesItems.itemCoalGenerator,
				LibVulpesItems.itemMotor,
				LibVulpesItems.itemAdvancedMotor,
				LibVulpesItems.itemEnhancedMotor,
				LibVulpesItems.itemEliteMotor);

		ItemTags.getCollection().getTagByID(new ResourceLocation("forge", "battery")).contains(LibVulpesItems.itemBattery);
		ItemTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesItems.itemMotor);
		ItemTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesItems.itemAdvancedMotor);
		ItemTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesItems.itemEnhancedMotor);
		ItemTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesItems.itemEliteMotor);
	}

	@SubscribeEvent
	public void registerContainers(RegistryEvent.Register<ContainerType<?>> evt) {
		LibvulpesGuiRegistry.initContainers(evt);
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
		LibVulpesTileEntityTypes.registerTileEntities(evt);
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerBlocks(RegistryEvent.Register<Block> evt) {
		IForgeRegistry<Block> r = evt.getRegistry();
		//Register Blocks
		r.register(LibVulpesBlocks.blockPhantom);
		r.register(LibVulpesBlocks.blockItemOutputHatch);
		r.register(LibVulpesBlocks.blockFluidOutputHatch);
		r.register(LibVulpesBlocks.blockItemInputHatch);
		r.register(LibVulpesBlocks.blockFluidInputHatch);
		r.register(LibVulpesBlocks.blockPlaceHolder);
		r.register(LibVulpesBlocks.blockMachineStructure);
		r.register(LibVulpesBlocks.blockCreativeInputPlug);
		r.register(LibVulpesBlocks.blockForgeInputPlug);
		r.register(LibVulpesBlocks.blockForgeOutputPlug);
		r.register(LibVulpesBlocks.blockCoalGenerator);

		r.register(LibVulpesBlocks.blockMotor);
		r.register(LibVulpesBlocks.blockAdvancedMotor);
		r.register(LibVulpesBlocks.blockEnhancedMotor);
		r.register(LibVulpesBlocks.blockEliteMotor);
		r.register(LibVulpesBlocks.blockAdvancedMachineStructure);

		//populate lists
		LibVulpesBlocks.motors = new Block[]{ LibVulpesBlocks.blockMotor, LibVulpesBlocks.blockAdvancedMotor, LibVulpesBlocks.blockEnhancedMotor, LibVulpesBlocks.blockEliteMotor };

		materialRegistry.registerOres(tabLibVulpesOres);

		//tag stuff
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesBlocks.blockMotor);
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesBlocks.blockAdvancedMotor);
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesBlocks.blockEnhancedMotor);
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge","motor")).contains(LibVulpesBlocks.blockEliteMotor);
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void missingMappings(RegistryEvent.MissingMappings<Item> evt) {
		for(Mapping<Item> mapping : evt.getAllMappings()) {
			if (mapping.key.compareTo(new ResourceLocation("libvulpes:productcrystal")) == 0)
				mapping.remap(MaterialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("GEM")).getItem());
		}
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void init(FMLCommonSetupEvent event) {
		proxy.init();
		PacketHandler.register();
		proxy.registerEventHandlers();
		registerBlockTypes();
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void postInit(FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(new BucketHandler());
		OreGen.injectOreGen();
	}

	private void registerBlockTypes() {
		List<BlockMeta> list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockItemOutputHatch.getDefaultState(), true));
		list.add(new BlockMeta(LibVulpesBlocks.blockItemOutputHatch.getDefaultState(), true));
		TileMultiBlock.addMapping('O', list);

		//Item Inputs
		list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockItemInputHatch.getDefaultState(), true));
		list.add(new BlockMeta(LibVulpesBlocks.blockItemInputHatch.getDefaultState(), true));
		TileMultiBlock.addMapping('I', list);

		//Power input
		list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockCreativeInputPlug.getDefaultState()));
		list.add(new BlockMeta(LibVulpesBlocks.blockForgeInputPlug.getDefaultState()));
		TileMultiBlock.addMapping('P', list);

		//Power output
		list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockForgeOutputPlug.getDefaultState()));
		TileMultiBlock.addMapping('p', list);

		//Liquid input
		list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockFluidInputHatch.getDefaultState(), true));
		list.add(new BlockMeta(LibVulpesBlocks.blockFluidInputHatch.getDefaultState(), true));
		TileMultiBlock.addMapping('L', list);

		//Liquid output
		list = new LinkedList<>();
		list.add(new BlockMeta(LibVulpesBlocks.blockFluidOutputHatch.getDefaultState(), true));
		list.add(new BlockMeta(LibVulpesBlocks.blockFluidOutputHatch.getDefaultState(), true));
		TileMultiBlock.addMapping('l', list);
	}
}

