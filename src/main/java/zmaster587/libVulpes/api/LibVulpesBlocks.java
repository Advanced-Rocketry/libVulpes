package zmaster587.libVulpes.api;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

public class LibVulpesBlocks {
	public static final Set<Block> blocks = new HashSet<>();

	public static Block blockHatch;
	public static Block blockPhantom;
	public static Block blockPlaceHolder;
	public static Block blockRFBattery;
	public static Block blockIC2Plug;
	public static Block blockRFOutput;
	public static Block blockStructureBlock;
	public static Block blockAdvStructureBlock;
	public static Block blockCreativeInputPlug;
	public static Block blockForgeInputPlug;
	public static Block blockForgeOutputPlug;
	public static Block blockMotor;
	public static Block blockAdvancedMotor;
	public static Block blockEnhancedMotor;
	public static Block blockEliteMotor;
	//public static List<Block> blockOre = new ArrayList<Block>();
	//public static List<Block> blockMetal = new ArrayList<Block>();
	public static Item[] itemOreProduct;
	//public static List<Block> blockCoil = new ArrayList<Block>();
	public static Block motors[];
	public static Block blockCoalGenerator;
	public static Block blockGTPlug;

	

	

	

	



	/**
	 * Register a Block with the default BlockItem class.
	 *
	 * @param block The Block instance
	 * @param <BLOCK>   The Block type
	 * @return The Block instance
	 */
	public static <BLOCK extends Block> BLOCK registerBlock(BLOCK block) {
		return registerBlock(block, BlockItem.class, true);
	}

	public static <ITEM extends Item> ITEM registerItem(ITEM item) {

		return (ITEM) GameData.register_impl(item);
	}

	/**
	 * Register a Block with a custom BlockItem class.
	 *
	 * @param <BLOCK>     The Block type
	 * @param block       The Block instance
	 * @param itemFactory A function that creates the BlockItem instance, or null if no BlockItem should be created
	 * @return The Block instance
	 */
	public static <BLOCK extends Block> BLOCK registerBlock(BLOCK block, Class<? extends BlockItem> clazz, boolean registerItemStates) {
		GameData.register_impl(block);

		blocks.add(block);
		return block;
	}
}
