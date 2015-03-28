package zmaster587.libVulpes;

import gregtech.api.enums.ItemList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zmaster587.libVulpes.tile.TileEntityPointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import zmaster587.libVulpes.util.Events;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="libVulpes",name="Vulpes library",version="1.6.4",useMetadata=true, dependencies="before:gregtech;after:RotaryCraft;after:GalacticraftCore;after:tconstruct;before:EnderIO")
public class LibVulpes {

	public static boolean debugMode;
	public static boolean forceLove;
	public static boolean ic2MinerGTOre;
	public static boolean gcSilicon;
	public static boolean diamondBlockHardness;
	public static boolean hardFactoryRecipe;
	public static boolean craftableSkyStone;
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		logger = event.getModLog();
		GameRegistry.registerTileEntity(TileEntityPointer.class, "TilePointer");
		GameRegistry.registerTileEntity(TileInventoriedPointer.class, "TileInvPointer");

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		forceLove = config.get(Configuration.CATEGORY_GENERAL, "forceGT_TC_love", false).getBoolean(false);
		debugMode = config.get(Configuration.CATEGORY_GENERAL, "debug", false).getBoolean(false);
		ic2MinerGTOre = config.get(Configuration.CATEGORY_GENERAL, "LetIC2MineGTOre", false).getBoolean(false);
		gcSilicon = config.get(Configuration.CATEGORY_GENERAL, "registerGCsiliconItems", false).getBoolean(false);
		diamondBlockHardness = config.get(Configuration.CATEGORY_GENERAL, "undoGTDiamondBlockMod", false).getBoolean(false);
		hardFactoryRecipe = config.get(Configuration.CATEGORY_GENERAL, "harderFactoryManager", false).getBoolean(false);
		craftableSkyStone = config.get(Configuration.CATEGORY_GENERAL, "addSkystoneCrafting", true).getBoolean(true);
		config.save();

		if((forceLove || ic2MinerGTOre))
			MinecraftForge.EVENT_BUS.register(new Events());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		if(craftableSkyStone && Loader.isModLoaded("appliedenergistics2")) {
			Block skystone = null;
			try {
				Class items = Class.forName("appeng.api.util.AEItemDefinition");
				Class blocks = Class.forName("appeng.api.definitions.Blocks");
				Class api = Class.forName("appeng.core.Api");
				skystone = (Block)items.getMethod("block").invoke(blocks.getDeclaredField("blockSkyStone").get(api.getMethod("blocks").invoke(api.getField("instance").get(null))));

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
		if(hardFactoryRecipe && Loader.isModLoaded("gregtech")) {
			List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
			Iterator<IRecipe> factory = recipes.iterator();
			Item factoryPart, factoryCable;
			try {
				factoryPart = Item.getItemFromBlock((Block)Class.forName("vswe.stevesfactory.blocks.ModBlocks").getField("blockManager").get(null));

				while (factory.hasNext()) {
					ItemStack is = factory.next().getRecipeOutput();
					if (is != null && is.getItem() == factoryPart) {
						factory.remove();
						logger.info("Removing old Steve's factory recipie");
					}
				}

				logger.info("Replacing old Steve's factory recipe");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(factoryPart,1), "xxx", "xyx", "zvz", 'x', "plateSteel", 'y', ItemList.Casing_MV.getItem() , 'z',  Items.redstone, 'v', Blocks.piston));

				factoryCable = Item.getItemFromBlock((Block)Class.forName("vswe.stevesfactory.blocks.ModBlocks").getField("blockCable").get(null));

				factory = recipes.iterator();

				while (factory.hasNext()) {
					ItemStack is = factory.next().getRecipeOutput();
					if (is != null && is.getItem() == factoryCable) {
						factory.remove();
						logger.info("Removing old Steve's cable recipe");
					}
				}

				logger.info("Replacing old Steve's cable recipe");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(factoryCable,8), "GPG", "IRI", "GPG", 'I', "plateSteel", 'G',Blocks.glass, 'R', Items.redstone, 'P', Blocks.light_weighted_pressure_plate));


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

