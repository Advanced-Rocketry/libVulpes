package zmaster587.libVulpes.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import zmaster587.libVulpes.LibVulpes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Events {

	//Hack to prevent inf recursion
	public static HashMap<String,ItemStack> gregAversionItems;

	public HashSet<Item> registeredOres = new HashSet<Item>();
	
	private boolean loadedGT = false;

	public Events() {
		gregAversionItems = new HashMap<String,ItemStack> ();
	}

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {
		String mod = Loader.instance().activeModContainer().getModId();

		
		if(LibVulpes.gcSilicon && (event.Name.contains("itemSilicon") || event.Name.contains("ingotSilicon"))) {
			
			LibVulpes.logger.error("Loading thing");
			try {
				Method mAddRecipe = Class.forName("micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes").getDeclaredMethod("addRecipe", ItemStack.class, ItemStack[].class);
				
				Field fBasicItem = Class.forName("micdoodle8.mods.galacticraft.core.items.GCItems").getField("basicItem");
				Item basicItem = (Item)fBasicItem.get(null);
				
				mAddRecipe.invoke(null, new ItemStack(basicItem, 9, 12), new ItemStack[] { new ItemStack(Items.diamond), event.Ore.copy(), event.Ore.copy(), new ItemStack(Items.redstone), new ItemStack(Items.dye, 1, 4) });
				mAddRecipe.invoke(null, new ItemStack(basicItem, 3, 13), new ItemStack[] { new ItemStack(Items.diamond), event.Ore.copy(), event.Ore.copy(), new ItemStack(Items.redstone), new ItemStack(Blocks.redstone_torch) });
				mAddRecipe.invoke(null, new ItemStack(basicItem, 1, 14), new ItemStack[] { new ItemStack(Items.diamond), event.Ore.copy(), event.Ore.copy(), new ItemStack(Items.redstone), new ItemStack(Items.repeater) });

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}

		if(LibVulpes.forceLove && mod.toLowerCase().contains("tconstruct")) {

			if(event.Name.toLowerCase().startsWith("ore") || event.Name.toLowerCase().startsWith("ingot") || event.Name.toLowerCase().startsWith("block"))
				gregAversionItems.put(event.Name,event.Ore);

			if(LibVulpes.debugMode)
				System.out.println("Adding " + event.Name);
		}
	}
}
