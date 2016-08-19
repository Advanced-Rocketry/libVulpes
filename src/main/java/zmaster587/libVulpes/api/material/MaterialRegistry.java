package zmaster587.libVulpes.api.material;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.util.ItemStackMapping;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MaterialRegistry {

	static HashMap<Object, MixedMaterial> mixedMaterialList = new HashMap<Object, MixedMaterial>();

	/**
	 * @param stack the item stack to get the material of
	 * @return {@link Materials} of the itemstack if it exists, otherwise null
	 */
	public static Materials getMaterialFromItemStack(ItemStack stack) {
		Item item = stack.getItem();

		//If items is an itemOreProduct it must have been registered

		for(Item i : LibVulpesItems.itemOreProduct) {
			if(item == i) {
				return Materials.values()[stack.getItemDamage()];
			}
		}

		int[] ids = OreDictionary.getOreIDs(stack);

		//Check all ores against list
		for(Materials ore : Materials.values()) {
			for(String str : ore.getOreDictNames()) {
				for(int i : ids) {
					if(OreDictionary.getOreName(i).contains(str)) 
						return ore;
				}
			}
		}

		return null;
	}

	/**
	 * @param material
	 * @param product
	 * @return an itemstack of size one containing the product with the given material, or null if one does not exist
	 */
	public static ItemStack getItemStackFromMaterialAndType(Materials material,AllowedProducts product) {
		return getItemStackFromMaterialAndType(material, product,1);
	}

	/**
	 * @param material
	 * @param product
	 * @param amount stackSize
	 * @return an itemstack of stackSize amount containing the product with the given material, or null if one does not exist
	 */
	public static ItemStack getItemStackFromMaterialAndType(Materials ore,AllowedProducts product, int amount) {
		return new ItemStack( LibVulpesItems.itemOreProduct[product.ordinal()], amount, ore.ordinal());
	}

	/**
	 * Registers a mixed material or allow to automate recipe registration
	 * @param material new mixed material to create
	 */
	public static void registerMixedMaterial(MixedMaterial material) {
		if(material.getInput() instanceof ItemStack)
			mixedMaterialList.put( new ItemStackMapping((ItemStack) material.getInput()), material);
		else
			mixedMaterialList.put( material.getInput(), material);
	}

	/**
	 * @param stack
	 * @return {@link MixedMaterial} that makes up the item, null if the item is not registered
	 */
	public MixedMaterial getMixedMaterial(ItemStack stack) {
		return mixedMaterialList.get(new ItemStackMapping(stack));
	}

	/**
	 * @param str
	 * @return mixed material corresponding to the supplied string example: "bronze"
	 */
	public MixedMaterial getMixedMaterial(String str) {
		return mixedMaterialList.get(str);
	}

	public static int getColorFromItemMaterial(ItemStack stack) {

		Materials material = getMaterialFromItemStack(stack);
		if(material == null) {
			return 0x7a7a7a;
			/*Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
			TextureAtlasSprite tas = (TextureAtlasSprite)stack.getIconIndex();
			
			IntBuffer pixels = BufferUtils.createIntBuffer(Math.max(tas.getIconWidth() * tas.getIconHeight(),1024));
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, tas.getOriginX(), tas.getOriginY(), tas.getIconWidth(), tas.getIconHeight(), GL11.GL_RGBA, GL11.GL_INT, pixels);
			int[] texture = new int[pixels.remaining()];
			pixels.get(texture);
			int number = 0;

			int r = 0,g = 0,b = 0;
			for (int i = 0; i < Math.sqrt(texture.length); i++) {
				for (int j = 0; j < Math.sqrt(texture.length); j++) {
					int pixel = texture[(i + (j * tas.getIconWidth()))];

					if((pixel & 0xFF) == 0xFF) {
						r += (pixel >>> 24) & 0xFF;
						g += (pixel >>> 16) & 0xFF;
						b += (pixel >>> 8) & 0xFF;
						number++;
					}
				}
			}

			return (((r / number)& 0xFF) << 16  ) | (((g / number)& 0xFF) << 8 ) | ((( b / number ) & 0xFF)   );*/

		}
		else
			return material.getColor();
	}

	/**
	 * @return Collection containing all registered mixed materials
	 */
	public static Collection<MixedMaterial> getMixedMaterialList() {
		return mixedMaterialList.values();
	}

	public static enum Materials {
		DILITHIUM("Dilithium", "pickaxe", 3, 0xddcecb, AllowedProducts.DUST.getFlagValue() | AllowedProducts.CRYSTAL.getFlagValue()),
		IRON("Iron", "pickaxe", 1, 0xafafaf, AllowedProducts.SHEET.getFlagValue() | AllowedProducts.STICK.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.PLATE.getFlagValue(), false),
		GOLD("Gold", "pickaxe", 1, 0xffff5d, AllowedProducts.DUST.getFlagValue() | AllowedProducts.PLATE.getFlagValue(), false),
		SILICON("Silicon", "pickaxe", 1, 0x2c2c2b, AllowedProducts.INGOT.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.BOULE.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.PLATE.getFlagValue(), false),
		COPPER("Copper", "pickaxe", 1, 0xd55e28, AllowedProducts.COIL.getFlagValue() | AllowedProducts.BLOCK.getFlagValue() | AllowedProducts.STICK.getFlagValue() | AllowedProducts.INGOT.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.PLATE.getFlagValue()),
		TIN("Tin", "pickaxe", 1, 0xcdd5d8, AllowedProducts.BLOCK.getFlagValue() | AllowedProducts.PLATE.getFlagValue() | AllowedProducts.INGOT.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.DUST.getFlagValue()),
		STEEL("Steel", "pickaxe", 1, 0x55555d, AllowedProducts.BLOCK.getFlagValue() | AllowedProducts.FAN.getFlagValue() | AllowedProducts.PLATE.getFlagValue() | AllowedProducts.INGOT.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.STICK.getFlagValue() | AllowedProducts.GEAR.getFlagValue() | AllowedProducts.SHEET.getFlagValue(), false),
		TITANIUM("Titanium", "pickaxe", 1, 0xb2669e, AllowedProducts.PLATE.getFlagValue() | AllowedProducts.INGOT.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.STICK.getFlagValue() | AllowedProducts.BLOCK.getFlagValue() | AllowedProducts.GEAR.getFlagValue() | AllowedProducts.SHEET.getFlagValue(), false),
		RUTILE("Rutile", "pickaxe", 1, 0xbf936a, AllowedProducts.ORE.getFlagValue(), new String[] {"Rutile", "Titanium"}),
		ALUMINUM("Aluminum", "pickaxe", 1, 0xb3e4dc, AllowedProducts.BLOCK.getFlagValue() | AllowedProducts.INGOT.getFlagValue() | AllowedProducts.PLATE.getFlagValue() | AllowedProducts.SHEET.getFlagValue() | AllowedProducts.DUST.getFlagValue() | AllowedProducts.NUGGET.getFlagValue() | AllowedProducts.SHEET.getFlagValue());

		String unlocalizedName, tool;
		String[] oreDictNames;
		int harvestLevel;
		int allowedProducts;
		int color;

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts, boolean hasOre) {
			this(unlocalizedName, tool, level, color, hasOre ? AllowedProducts.ORE.getFlagValue() | allowedProducts : allowedProducts, new String[] {unlocalizedName});
		}

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts, MixedMaterial ... products) {
			this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.ORE.getFlagValue(), new String[] {unlocalizedName});
		}

		private Materials(String unlocalizedName, String tool, int level, int color, int allowedProducts) {
			this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.ORE.getFlagValue(), new String[] {unlocalizedName});
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
			if(product.isBlock()) {
				return new ItemStack(product.blockArray.get(this.ordinal()/16), amount, getMeta());
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
			return LibVulpesBlocks.blockOre.get(this.ordinal()/16);
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
