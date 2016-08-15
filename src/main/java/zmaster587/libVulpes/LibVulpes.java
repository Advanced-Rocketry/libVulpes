package zmaster587.libVulpes;


import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="libVulpes",name="Vulpes library",version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@",useMetadata=true, dependencies="before:gregtech;after:CoFHCore;after:BuildCraft|Core")
public class LibVulpes {
	public static Logger logger;
	
	public static Item itemLinker;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		
		GameRegistry.registerTileEntity(TilePointer.class, "TilePointer");
		GameRegistry.registerTileEntity(TileInventoriedPointer.class, "TileInvPointer");
		
		itemLinker = new ItemLinker().setUnlocalizedName("Linker").setCreativeTab(CreativeTabs.tabTransport).setTextureName("libvulpes:Linker");
		
		GameRegistry.registerItem(itemLinker, "Linker");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addShapedRecipe(new ItemStack(itemLinker), "x","y","z", 'x', Items.redstone, 'y', Items.gold_ingot, 'z', Items.iron_ingot);

	}
}

