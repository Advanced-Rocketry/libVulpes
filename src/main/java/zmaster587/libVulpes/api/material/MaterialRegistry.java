package zmaster587.libVulpes.api.material;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.Material.Materials;
import zmaster587.libVulpes.block.BlockCoil;
import zmaster587.libVulpes.block.BlockMetalBlock;
import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.item.ItemOre;
import zmaster587.libVulpes.item.ItemOreProduct;
import zmaster587.libVulpes.util.ItemStackMapping;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MaterialRegistry {

	static HashMap<Object, MixedMaterial> mixedMaterialList = new HashMap<Object, MixedMaterial>();
	static HashMap<AllowedProducts, List<Block>> productBlockListMapping;
	
	
	public MaterialRegistry() {
		productBlockListMapping = new HashMap<AllowedProducts, List<Block>>();
	}
	
	//TODO: allow more block types
	public void registerOres(CreativeTabs tab) {
		int len = Materials.values().length;
		int numberOfOreBlocks = (len/16) + 1;
		BlockOre ores;
		BlockOre metalBlocks;
		BlockOre coilBlocks;
		
		List<AllowedProducts> allAllowedProducts = AllowedProducts.getAllAllowedProducts();

		LibVulpesItems.itemOreProduct = new ItemOreProduct[allAllowedProducts.size()];

		for(int i = 0; i < allAllowedProducts.size(); i++) {

			if(!allAllowedProducts.get(i).isBlock()) {
				LibVulpesItems.itemOreProduct[i] = new ItemOreProduct(allAllowedProducts.get(i).name().toLowerCase()).setCreativeTab(tab);
				GameRegistry.registerItem(LibVulpesItems.itemOreProduct[i], "product" + allAllowedProducts.get(i).getName().toLowerCase());
			}
		}

		for(int i = 0; i < numberOfOreBlocks; i++) {

			String name = "ore";
			String metalBlockName = "metal";
			String coilName = "coil";

			metalBlocks = new BlockMetalBlock(Material.rock);
			metalBlocks.setBlockName(metalBlockName).setCreativeTab(tab).setHardness(4f).setBlockTextureName("block");
			metalBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			metalBlocks.product = AllowedProducts.getProductByName("BLOCK");

			ores = new BlockOre(Material.rock);
			ores.setBlockName(name).setCreativeTab(tab).setHardness(4f).setBlockTextureName("ore");
			ores.numBlocks = (byte)Math.min(len - (16*i), 16);
			ores.product = AllowedProducts.getProductByName("ORE");

			coilBlocks = new BlockCoil(Material.rock, "libvulpes:coilSide", "libvulpes:coilPole");
			coilBlocks.setBlockName(coilName).setCreativeTab(tab).setHardness(4f).setBlockTextureName("coil");
			coilBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			coilBlocks.product = AllowedProducts.getProductByName("COIL");
			
			GameRegistry.registerBlock(ores, ItemOre.class, name + i);
			GameRegistry.registerBlock(metalBlocks, ItemOre.class, metalBlockName + i);
			GameRegistry.registerBlock(coilBlocks, ItemOre.class, coilName + i);

			for(int j = 0; j < 16 && j < 16*i + (len % 16); j++) {
				int index = i*16 + j;
				Materials ore = Materials.values()[index];

				ores.ores[j] = ore;
				ores.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);

				metalBlocks.ores[j] = ore;
				metalBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);

				coilBlocks.ores[j] = ore;
				coilBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);

				for(AllowedProducts product : allAllowedProducts) {
					if(!product.isBlock() && product.isOfType(ore.getAllowedProducts()))
						((ItemOreProduct)LibVulpesItems.itemOreProduct[product.ordinal()]).registerItem(index, ore);
				}

				for(String str : ore.getOreDictNames()) {
					if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()))
						OreDictionary.registerOre("ore" + str, new ItemStack(ores, 1 , j));
					if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts()))
						OreDictionary.registerOre("block" + str, new ItemStack(metalBlocks, 1 , j));
					if(AllowedProducts.getProductByName("COIL").isOfType(ore.getAllowedProducts()))
						OreDictionary.registerOre("coil" + str, new ItemStack(coilBlocks, 1 , j));
				}
			}
			
			getBlockListForProduct(AllowedProducts.getProductByName("BLOCK")).add(metalBlocks);
			getBlockListForProduct(AllowedProducts.getProductByName("COIL")).add(coilBlocks);
			getBlockListForProduct(AllowedProducts.getProductByName("ORE")).add(ores);
		}
	}
	
	public List<Block> getBlockListForProduct(AllowedProducts product) {
		return productBlockListMapping.get(product);
	}
	
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
	
	
}
