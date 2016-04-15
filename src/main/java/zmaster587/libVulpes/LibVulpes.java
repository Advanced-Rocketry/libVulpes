package zmaster587.libVulpes;


import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import zmaster587.libVulpes.util.Events;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="libVulpes",name="Vulpes library",version="0.0.6e",useMetadata=true, dependencies="before:gregtech;after:CoFHCore")
public class LibVulpes {

	public static boolean debugMode;
	public static boolean forceLove;
	public static boolean ic2MinerGTOre;
	public static boolean gcSilicon;
	public static boolean diamondBlockHardness;
	public static boolean hardFactoryRecipe;
	public static boolean craftableSkyStone;
	public static Logger logger;
	
	public static Item itemLinker;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		logger = event.getModLog();
		GameRegistry.registerTileEntity(TilePointer.class, "TilePointer");
		GameRegistry.registerTileEntity(TileInventoriedPointer.class, "TileInvPointer");

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		forceLove = config.get(Configuration.CATEGORY_GENERAL, "forceGT_TC_love", false).getBoolean(false);
		debugMode = config.get(Configuration.CATEGORY_GENERAL, "debug", false).getBoolean(false);
		gcSilicon = config.get(Configuration.CATEGORY_GENERAL, "registerGCsiliconItems", false).getBoolean(false);
		diamondBlockHardness = config.get(Configuration.CATEGORY_GENERAL, "undoGTDiamondBlockMod", false).getBoolean(false);
		//hardFactoryRecipe = config.get(Configuration.CATEGORY_GENERAL, "harderFactoryManager", false).getBoolean(false);
		craftableSkyStone = config.get(Configuration.CATEGORY_GENERAL, "addSkystoneCrafting", false).getBoolean(true);
		config.save();

		if((forceLove || ic2MinerGTOre))
			MinecraftForge.EVENT_BUS.register(new Events());
		
		itemLinker = new ItemLinker().setUnlocalizedName("Linker").setCreativeTab(CreativeTabs.tabTransport).setTextureName("libvulpes:Linker");
		
		GameRegistry.registerItem(itemLinker, "Linker");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addShapedRecipe(new ItemStack(this.itemLinker), "x","y","z", 'x', Items.redstone, 'y', Items.gold_ingot, 'z', Items.iron_ingot);

	}
	
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		if(craftableSkyStone && Loader.isModLoaded("appliedenergistics2")) {
			Block skystone = null;
			try {
				Class items = Class.forName("appeng.api.util.AEItemDefinition");
				Class blocks = Class.forName("appeng.api.definitions.Blocks");
				Class api = Class.forName("appeng.core.Api");
				skystone = (Block)items.getMethod("block").invoke(blocks.getDeclaredField("blockSkyStone").get(api.getMethod("blocks").invoke(api.getField("INSTANCE").get(null))));

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(skystone != null) {
				GameRegistry.addShapedRecipe(new ItemStack(skystone, 32), "xxx", "xyx", "xxx", 'x', Blocks.obsidian, 'y', Blocks.stone);	
			}
			
		}

		if(forceLove) {
			for(String item : Events.gregAversionItems.keySet()) {

				ArrayList<ItemStack> list = OreDictionary.getOres(item);

				for (int i = list.size(); i --> 0; )
				{
					OreDictionary.registerOre(item, list.get(i));

					if(LibVulpes.debugMode)
						logger.info("Registering " + item);
				}
			}
		}
	}
}

