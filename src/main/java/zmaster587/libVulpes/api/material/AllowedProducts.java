package zmaster587.libVulpes.api.material;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;

public class AllowedProducts {
	
	/**
	 * Registers a product with the handler
	 * The product is then registered with allowed materials using the name of the product as the prefix
	 * The name of the product is also the name of the icon to use with the product (ie: "rod" uses "rod.png")
	 * @param name name of the product
	 */
	public static void registerProduct(String name) {
		AllowedProducts product = new AllowedProducts();
		product.flagValue = currentFlagValue;
		product.name = name;
		currentFlagValue++;
		map.put(name, new AllowedProducts());
	}

	public static void registerProduct(String name, boolean isBlock, List<Block> blockArray) {
		AllowedProducts product = new AllowedProducts(isBlock, blockArray);
		product.flagValue = currentFlagValue;
		product.name = name;
		currentFlagValue++;
		map.put(name, new AllowedProducts());
	}
	
	public static AllowedProducts getProductByName(String name) {
		return map.get(name);
	}
	
	private static short currentFlagValue = 1;
	private static HashMap<String, AllowedProducts> map = new HashMap<String, AllowedProducts>();
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

	short flagValue = 0;
	String name;
	boolean isBlock;
	List<Block> blockArray;

	private AllowedProducts() {
		this.isBlock = false;
	}

	private AllowedProducts(boolean isBlock,List<Block> blockArray) {
		this.isBlock = isBlock;
		this.blockArray = blockArray;
	}

	public int getFlagValue() {
		return 1 << flagValue;
	}

	/**
	 * @param flag
	 * @return true if the flag corresponds to this type of item
	 */
	public boolean isOfType(int flag) {
		return (getFlagValue() & flag) != 0;
	}
	
	public String getName() {
		return this.name.equalsIgnoreCase("stick") ? "rod" : this.name;
	}

	/**
	 * @return true if the itemtype is a block, IE Ore, coils, etc
	 */
	public boolean isBlock() {
		return isBlock;
	}
}
