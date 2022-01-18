package zmaster587.libVulpes.api;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraftforge.registries.GameData;


public class LibVulpesBlocks {
	public static final Set<Block> blocks = new HashSet<>();

	public static Block blockItemOutputHatch;
	public static Block blockFluidOutputHatch;
	public static Block blockItemInputHatch;
	public static Block blockFluidInputHatch;
	public static Block blockPhantom;
	public static Block blockPlaceHolder;
	public static Block blockMachineStructure;
	public static Block blockAdvancedMachineStructure;
	public static Block blockCreativeInputPlug;
	public static Block blockForgeInputPlug;
	public static Block blockForgeOutputPlug;
	public static Block blockMotor;
	public static Block blockAdvancedMotor;
	public static Block blockEnhancedMotor;
	public static Block blockEliteMotor;
	public static Block[] motors;
	public static Block blockCoalGenerator;


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
