package zmaster587.libVulpes.api.material;

import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Material {
	
	public static enum Materials {
		DILITHIUM("Dilithium", "pickaxe", 3, 0xddcecb, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("CRYSTAL").getFlagValue()),
		IRON("Iron", "pickaxe", 1, 0xafafaf, AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false),
		GOLD("Gold", "pickaxe", 1, 0xffff5d, AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false),
		SILICON("Silicon", "pickaxe", 1, 0x2c2c2b, AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("BOULE").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue(), false),
		COPPER("Copper", "pickaxe", 1, 0xd55e28, AllowedProducts.getProductByName("COIL").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue()),
		TIN("Tin", "pickaxe", 1, 0xcdd5d8, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue()),
		STEEL("Steel", "pickaxe", 1, 0x55555d, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("FAN").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false),
		TITANIUM("Titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false),
		RUTILE("Rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {"Rutile", "Titanium"}),
		ALUMINUM("Aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue());

		String unlocalizedName, tool;
		String[] oreDictNames;
		int harvestLevel;
		int allowedProducts;
		int color;
		//Hack assigning in libvulpes preinit
		public static MaterialRegistry registry;
		
		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts, boolean hasOre) {
			this(unlocalizedName, tool, level, color, hasOre ? AllowedProducts.getProductByName("ORE").getFlagValue() | allowedProducts : allowedProducts, new String[] {unlocalizedName});
		}

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts, MixedMaterial ... products) {
			this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {unlocalizedName});
		}

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts) {
			this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {unlocalizedName});
		}

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts, String[] oreDictNames) {
			this.unlocalizedName = unlocalizedName;
			this.tool = tool;
			this.harvestLevel = level;
			this.oreDictNames = oreDictNames;
			this.allowedProducts = allowedProducts;
			this.color = color;
		}

		/**
		 * @return true if the material is vanilla (Gold, iron)
		 */
		public boolean isVanilla() {
			return this.unlocalizedName.equals("Iron") ||  this.unlocalizedName.equals("Gold");
		}

		/**
		 * @param product
		 * @param amount
		 * @return Itemstack representing the product of this material, or null if nonexistant
		 */
		public ItemStack getProduct(AllowedProducts product, int amount) {
			/*ItemStack stack = OreDictionary.getOres(product.getName() + this.name()).get(0);
			stack = stack.copy();
			stack.stackSize = amount;
			return stack;*/

			if(product.isBlock()) {
				return new ItemStack(registry.getBlockListForProduct(product).get(this.ordinal()/16), amount, getMeta());
			}
			return new ItemStack(LibVulpesItems.itemOreProduct[product.ordinal()], amount, getMeta());
		}
		/**
		 * @param product
		 * @return Itemstack of size 1 representing the product of this material, or null if nonexistant
		 */
		public ItemStack getProduct(AllowedProducts product) {
			return getProduct(product,1);
		}

		/**
		 * @return 32wide-bitmask corresponding to allowed products by this material
		 */
		public int getAllowedProducts() {
			return allowedProducts;
		}

		/**
		 * @return harvest level required to harvest the ore of this material
		 */
		public int getHarvestLevel() {
			return harvestLevel;
		}

		/**
		 * @return tool required to harvest the ore of this material
		 */
		public String getTool() {
			return tool;
		}

		public String getUnlocalizedName() {
			return unlocalizedName;
		}

		/**
		 * @return list of ore dictionary names for this material.  Example: {iron, pigiron}
		 */
		public String[] getOreDictNames() {
			return oreDictNames;
		}

		/**
		 * Used in rendering of the item and block
		 * @return color of the material 0xRRGGBB
		 */
		public int getColor() {
			return color;
		}

		@Deprecated
		public Block getBlock() {
			return registry.getBlockListForProduct(AllowedProducts.getProductByName("ORE")).get(this.ordinal()/16);
			//return LibVulpesBlocks.blockOre.get(this.ordinal()/16);
		}

		/**
		 * @return the meta value for the itemstack representing a block of this material
		 */
		public int getMeta() {
			return this.ordinal() % 16;
		}

		/**
		 * 
		 * @param str 
		 * @return the material corresponding to the string supplied or null if non existant
		 */
		public static Materials valueOfSafe(String str) {
			try {
				return Materials.valueOf(str);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
