package zmaster587.libVulpes;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.event.TickEvent;
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
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.config.LibVulpesConfig;
import zmaster587.libVulpes.event.BucketHandler;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockPhantom;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.block.multiblock.BlockMultiMachineBattery;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.libVulpes.client.ClientProxy;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.tile.energy.TileCreativePowerInput;
import zmaster587.libVulpes.tile.energy.TileForgePowerInput;
import zmaster587.libVulpes.tile.energy.TileForgePowerOutput;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

// name="Vulpes library",version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@",useMetadata=true, dependencies="after:ic2;after:cofhcore;after:buildcraft|core"
@Mod(LibVulpes.MODID)
public class LibVulpes {
	public static org.apache.logging.log4j.Logger logger = LogManager.getLogger("libVulpes");
	public static int time = 0;
	private static HashMap<Class, String> userModifiableRecipes = new HashMap<Class, String>();

	public final static String MODID = "libvulpes";
	public static LibVulpes instance;
	public static ModContainer MOD_CONTAINER;
	//Classload

	public static CommonProxy proxy = (CommonProxy) DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	private static ItemGroup tabMultiblock = new ItemGroup("multiBlock") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(LibVulpesItems.itemLinker);// AdvancedRocketryItems.itemSatelliteIdChip;
		}
	};

	public static ItemGroup tabLibVulpesOres = new ItemGroup("advancedRocketryOres") {

		@Override
		public ItemStack createIcon() {
			return MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("ORE"));
		}
	};

	public static MaterialRegistry materialRegistry = new MaterialRegistry();

	public static void registerRecipeHandler(Class clazz, String fileName) {
		userModifiableRecipes.put(clazz, fileName);
	}
	
	public LibVulpes()
    {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

        //Initialze Blocks
        LibVulpesBlocks.blockPhantom = new BlockPhantom(net.minecraft.block.AbstractBlock.Properties.create(Material.MISCELLANEOUS)).setRegistryName("block_phantom");
        LibVulpesBlocks.blockHatch = new BlockHatch(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3)).setRegistryName("hatch");
        LibVulpesBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setRegistryName("place_holder");
        LibVulpesBlocks.blockAdvStructureBlock = new BlockAlphaTexture(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3)).setRegistryName("adv_structure_machine");
        LibVulpesBlocks.blockStructureBlock = new BlockAlphaTexture(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3)).setRegistryName("structure_machine");
        LibVulpesBlocks.blockCreativeInputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3), TileCreativePowerInput.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("creative_power_battery");
        LibVulpesBlocks.blockForgeInputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f), TileForgePowerInput.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("forge_power_input"); 
        LibVulpesBlocks.blockForgeOutputPlug = new BlockMultiMachineBattery(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f), TileForgePowerOutput.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("forge_power_output");
        LibVulpesBlocks.blockCoalGenerator = new BlockTile(net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f), GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("coal_generator");
        ((BlockTile)LibVulpesBlocks.blockCoalGenerator)._setTile( LibVulpesTileEntityTypes.TILE_COAL_GENERATOR);
        //LibVulpesBlocks.blockRFBattery = new BlockMultiMachineBattery(Material.ROCK, TilePlugInputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("rfBattery").setCreativeTab(tabMultiblock).setHardness(3f);
        //LibVulpesBlocks.blockRFOutput = new BlockMultiMachineBattery(Material.ROCK, TilePlugOutputRF.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("rfOutput").setCreativeTab(tabMultiblock).setHardness(3f);

        net.minecraft.block.AbstractBlock.Properties motorProperties = net.minecraft.block.AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2);
        LibVulpesBlocks.blockMotor = new BlockMotor(motorProperties, 1f).setRegistryName("motor");
        LibVulpesBlocks.blockAdvancedMotor = new BlockMotor(motorProperties, 1/1.5f).setRegistryName("advanced_motor");
        LibVulpesBlocks.blockEnhancedMotor = new BlockMotor(motorProperties, 1/2f).setRegistryName("enhanced_motor");
        LibVulpesBlocks.blockEliteMotor = new BlockMotor(motorProperties, 1/4f).setRegistryName("elite_motor");
        
        //if(Loader.isModLoaded("ic2"))
        //	LibVulpesBlocks.blockIC2Plug = new BlockMultiMachineBattery(Material.ROCK, TilePlugInputIC2.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("forgePowerInput").setCreativeTab(tabMultiblock).setHardness(3f);
        
        //if(Loader.isModLoaded("gregtech"))
        //	LibVulpesBlocks.blockGTPlug = new BlockMultiMachineBattery(Material.ROCK, TilePlugInputGregTech.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("gregPowerInput").setCreativeTab(tabMultiblock).setHardness(3f);
        
        
        //Initialize Items
        LibVulpesItems.itemLinker = new ItemLinker(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("linker");
        LibVulpesItems.itemBattery = new Item(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("smallbattery");
        LibVulpesItems.itemBatteryx2 = new Item(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("small2xbattery");
        LibVulpesItems.itemHoloProjector = new ItemProjector(new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("holo_projector");
        
        LibVulpesItems.itemHatch = new BlockItem(LibVulpesBlocks.blockHatch, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("hatch");
        LibVulpesItems.itemAdvStructureBlock = new BlockItem(LibVulpesBlocks.blockAdvStructureBlock, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("adv_structure_machine");
        LibVulpesItems.itemStructureBlock = new BlockItem(LibVulpesBlocks.blockStructureBlock, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("structure_machine");
        LibVulpesItems.itemCreativeInputPlug = new BlockItem(LibVulpesBlocks.blockCreativeInputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("creative_power_battery");
        LibVulpesItems.itemForgeInputPlug = new BlockItem(LibVulpesBlocks.blockForgeInputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("forge_power_input"); 
        LibVulpesItems.itemForgeOutputPlug = new BlockItem(LibVulpesBlocks.blockForgeOutputPlug, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("forge_power_output");
        LibVulpesItems.itemCoalGenerator = new BlockItem(LibVulpesBlocks.blockCoalGenerator, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("coal_generator");
        LibVulpesItems.itemMotor = new BlockItem(LibVulpesBlocks.blockMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("motor");
        LibVulpesItems.itemAdvancedMotor = new BlockItem(LibVulpesBlocks.blockAdvancedMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("advanced_motor");
        LibVulpesItems.itemEnhancedMotor = new BlockItem(LibVulpesBlocks.blockEnhancedMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("enhanced_motor");
        LibVulpesItems.itemEliteMotor = new BlockItem(LibVulpesBlocks.blockEliteMotor, new net.minecraft.item.Item.Properties().group(tabMultiblock)).setRegistryName("elite_motor");
        
        
        
        
		proxy.preinit();
		//Configuration
		LibVulpesConfig.register();
		//TeslaCapabilityProvider.registerCap();


        /*DUST,
        INGOT,
        GEM,
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
        AllowedProducts.registerProduct("GEM");
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

        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("dilithium", "pickaxe", 3, 0xddcecb, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("GEM").getFlagValue()));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("iron", "pickaxe", 1, 0xafafaf, AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("gold", "pickaxe", 1, 0xffff5d, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("silicon", "pickaxe", 1, 0x2c2c2b, AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("BOULE").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("copper", "pickaxe", 1, 0xd55e28, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("tin", "pickaxe", 1, 0xcdd5d8, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue()));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("steel", "pickaxe", 1, 0x55555d, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("FAN").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {"rutile", "titanium"}));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue()));
        materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("iridium", "pickaxe", 2, 0xdedcce, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue()));

		//
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public void registerItems(RegistryEvent.Register<Item> evt)
    {
        //Register Items
        
        evt.getRegistry().registerAll(LibVulpesItems.itemLinker,
        		LibVulpesItems.itemBattery,
        		LibVulpesItems.itemBatteryx2,
        		LibVulpesItems.itemHoloProjector,
        		LibVulpesItems.itemHatch,
        		LibVulpesItems.itemAdvStructureBlock,
        		LibVulpesItems.itemStructureBlock,
        		LibVulpesItems.itemCreativeInputPlug,
        		LibVulpesItems.itemForgeInputPlug,
        		LibVulpesItems.itemForgeOutputPlug,
        		LibVulpesItems.itemCoalGenerator,
        		LibVulpesItems.itemMotor,
        		LibVulpesItems.itemAdvancedMotor,
        		LibVulpesItems.itemEnhancedMotor,
        		LibVulpesItems.itemEliteMotor);
        
        ItemTags.getCollection().func_241834_b(new ResourceLocation("forge", "itembattery")).func_230235_a_(LibVulpesItems.itemBattery);
        ItemTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesItems.itemMotor);
        ItemTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesItems.itemAdvancedMotor);
        ItemTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesItems.itemEnhancedMotor);
        ItemTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesItems.itemEliteMotor);
    }
    
    @SubscribeEvent
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> evt)
    {
    	LibvulpesGuiRegistry.initContainers(evt);
    }
    
	@OnlyIn(value=Dist.CLIENT)
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		proxy.preInitItems();
		proxy.preInitBlocks();
	}
    

    /*@EventHandler
    public void registerRecipes(FMLInitializationEvent evt)
    {
   
//
      
//      
//      //Plugs
        /*if(Loader.isModLoaded("ic2")) {
          toRegister.add(new ShapelessOreRecipe(null, new ItemStack(LibVulpesBlocks.blockIC2Plug), LibVulpesBlocks.blockStructureBlock, 
                  IC2Items.getItem("te","mv_transformer"), LibVulpesItems.itemBattery).setRegistryName(new ResourceLocation("libvulpes", "blockIC2Plug")));
        }
        if(Loader.isModLoaded("gregtech")) {
          toRegister.add(new ShapelessOreRecipe(null, new ItemStack(LibVulpesBlocks.blockGTPlug), LibVulpesBlocks.blockStructureBlock, 
                  "plateBatteryAlloy","plateBatteryAlloy", LibVulpesItems.itemBattery).setRegistryName(new ResourceLocation("libvulpes", "blockGTPlug")));
        }* /
        
//      //GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockRFBattery), new ItemStack(LibVulpesBlocks.blockRFOutput));
//      //GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockRFOutput), new ItemStack(LibVulpesBlocks.blockRFBattery));
    }*/
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt)
	{
		LibVulpesTileEntityTypes.registerTileEntities(evt);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		IForgeRegistry<Block> r = evt.getRegistry();
        //Register Blocks
		r.register(LibVulpesBlocks.blockPhantom);
		r.register(LibVulpesBlocks.blockHatch);
		r.register(LibVulpesBlocks.blockPlaceHolder);
		r.register(LibVulpesBlocks.blockStructureBlock);
		r.register(LibVulpesBlocks.blockCreativeInputPlug);
		r.register(LibVulpesBlocks.blockForgeInputPlug);
		r.register(LibVulpesBlocks.blockForgeOutputPlug);
		r.register(LibVulpesBlocks.blockCoalGenerator);

		r.register(LibVulpesBlocks.blockMotor);
		r.register(LibVulpesBlocks.blockAdvancedMotor);
		r.register(LibVulpesBlocks.blockEnhancedMotor);
		r.register(LibVulpesBlocks.blockEliteMotor);
        //r.register(LibVulpesBlocks.blockRFBattery.setRegistryName(LibVulpesBlocks.blockRFBattery.getUnlocalizedName()));
        //r.register(LibVulpesBlocks.blockRFOutput.setRegistryName(LibVulpesBlocks.blockRFOutput.getUnlocalizedName()));
		r.register(LibVulpesBlocks.blockAdvStructureBlock);

        //populate lists
        Block motors[] = { LibVulpesBlocks.blockMotor, LibVulpesBlocks.blockAdvancedMotor, LibVulpesBlocks.blockEnhancedMotor, LibVulpesBlocks.blockEliteMotor };
        LibVulpesBlocks.motors = motors;




        //MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------
        //Items dependant on IC2
        /*if(Loader.isModLoaded("ic2")) {
            LibVulpesBlocks.blockIC2Plug = new BlockMultiMachineBattery(Material.ROCK ,TilePlugInputIC2.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("IC2Plug").setCreativeTab(tabMultiblock).setHardness(3f);
            LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockIC2Plug.setRegistryName( LibVulpesBlocks.blockIC2Plug.getUnlocalizedName().substring(5)));
            GameRegistry.registerTileEntity(TilePlugInputIC2.class, "ARIC2Plug");
        }
        
        if(Loader.isModLoaded("gregtech")) {
            LibVulpesBlocks.blockGTPlug = new BlockMultiMachineBattery(Material.ROCK ,TilePlugInputGregTech.class, GuiHandler.guiId.MODULAR.ordinal()).setRegistryName("GTPlug").setCreativeTab(tabMultiblock).setHardness(3f);
            LibVulpesBlocks.registerBlock(LibVulpesBlocks.blockGTPlug.setRegistryName( LibVulpesBlocks.blockGTPlug.getUnlocalizedName().substring(5)));
            GameRegistry.registerTileEntity(TilePlugInputGregTech.class, "ARGTPlug");
        }


        if(FMLCommonHandler.instance().getSide().isClient()) {
            //Register Block models
            Item blockItem = Item.getItemFromBlock(LibVulpesBlocks.blockHatch);
            ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("libvulpes:inputHatch", "inventory"));
            ModelLoader.setCustomModelResourceLocation(blockItem, 1, new ModelResourceLocation("libvulpes:outputHatch", "inventory"));
            ModelLoader.setCustomModelResourceLocation(blockItem, 2, new ModelResourceLocation("libvulpes:fluidInputHatch", "inventory"));
            ModelLoader.setCustomModelResourceLocation(blockItem, 3, new ModelResourceLocation("libvulpes:fluidOutputHatch", "inventory"));
        }*/
        
        materialRegistry.registerOres(tabLibVulpesOres);
        
        //Ore dict stuff
        BlockTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesBlocks.blockMotor);
        BlockTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesBlocks.blockAdvancedMotor);
        BlockTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesBlocks.blockEnhancedMotor);
        BlockTags.getCollection().func_241834_b(new ResourceLocation("forge","motor")).func_230235_a_(LibVulpesBlocks.blockEliteMotor);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void missingMappings(RegistryEvent.MissingMappings<Item> evt)
	{
		for(Mapping<Item> mapping : evt.getAllMappings())
		{
			if (mapping.key.compareTo(new ResourceLocation("libvulpes:productcrystal")) == 0)
				mapping.remap(MaterialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("GEM")).getItem());
			
		}
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void init(FMLCommonSetupEvent event) {
		proxy.init();
		PacketHandler.register();
		//NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		proxy.registerEventHandlers();
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void postInit(FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(new BucketHandler());
		
		//Init TileMultiblock
		//Item output
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 1), false));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 9), false));
		TileMultiBlock.addMapping('O', list);

		//Item Inputs
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 0), false));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 8), false));
		TileMultiBlock.addMapping('I', list);

		//Power input
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockCreativeInputPlug.getDefaultState()));
		list.add(new BlockMeta(LibVulpesBlocks.blockForgeInputPlug.getDefaultState()));
		if(LibVulpesBlocks.blockRFBattery != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockRFBattery.getDefaultState()));
		if(LibVulpesBlocks.blockIC2Plug != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockIC2Plug.getDefaultState()));
		TileMultiBlock.addMapping('P', list);

		//Power output
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockForgeOutputPlug.getDefaultState()));
		if(LibVulpesBlocks.blockRFOutput != null)
			list.add(new BlockMeta(LibVulpesBlocks.blockRFOutput.getDefaultState()));
		TileMultiBlock.addMapping('p', list);

		//Liquid input
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 2), false));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 10), false));
		TileMultiBlock.addMapping('L', list);

		//Liquid output
		list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 3), false));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch.getDefaultState().with(BlockHatch.VARIANT, 11), false));
		TileMultiBlock.addMapping('l', list);

		//User Recipes


	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		time++;
	}
}

