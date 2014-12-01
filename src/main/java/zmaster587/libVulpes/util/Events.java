package zmaster587.libVulpes.util;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import zmaster587.libVulpes.LibVulpes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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

		//Really dirty way of registering GT ores to IC2 scanner... has to be done quickly so fix later...
		if(LibVulpes.ic2MinerGTOre && mod.toLowerCase().contains("greg") && event.Name.toLowerCase().contains("ore") && !registeredOres.contains(event.Ore.getItem())) {
			loadedGT = true;
			
			registeredOres.add(event.Ore.getItem());
			
			LibVulpes.logger.info("The fox says the wizard is registering Gregtech ore, " + event.Name + ", to the IC2 scanner");
			
			try {

				Class.forName("ic2.core.IC2").getMethod("addValuableOre", IRecipeInput.class, int.class).invoke(null, new RecipeInputItemStack( new ItemStack(event.Ore.getItem(), 1,2)), 1);
				Class.forName("ic2.core.IC2").getMethod("addValuableOre", IRecipeInput.class, int.class).invoke(null, new RecipeInputItemStack( new ItemStack(event.Ore.getItem(), 1,1)), 1);
				Class.forName("ic2.core.IC2").getMethod("addValuableOre", IRecipeInput.class, int.class).invoke(null, new RecipeInputItemStack( new ItemStack(event.Ore.getItem(), 1,3)), 1);
				LibVulpes.logger.info("Success!");
			} catch (Exception e) {
				LibVulpes.logger.warn(e.getMessage());
				LibVulpes.logger.warn("The wizard was killed by Voldemort");
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
