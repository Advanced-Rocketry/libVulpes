package zmaster587.libVulpes.api.material;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.items.ItemMaterialBlock;
import zmaster587.libVulpes.items.ItemOreProduct;
import zmaster587.libVulpes.util.ItemStackMapping;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class MaterialRegistry {

	static HashMap<Object, MixedMaterial> mixedMaterialList = new HashMap<>();
	static HashMap<AllowedProducts, List<Block>> productBlockListMapping;
	static List<MaterialRegistry> registries = new LinkedList<>();

	@OnlyIn(value=Dist.CLIENT)
	static Object oreProductColorizer;

	public HashMap<String, zmaster587.libVulpes.api.material.Material> strToMaterial = new HashMap<String, zmaster587.libVulpes.api.material.Material>();
	public List<zmaster587.libVulpes.api.material.Material> materialList = new LinkedList<zmaster587.libVulpes.api.material.Material>();
	public HashMap<AllowedProducts, HashMap<zmaster587.libVulpes.api.material.Material, Item>> oreProducts;


	public MaterialRegistry() {
		productBlockListMapping = new HashMap<>();
		registries.add(this);
	}

	public void registerMaterial(zmaster587.libVulpes.api.material.Material material) {
		strToMaterial.put(material.getUnlocalizedName(), material);
		material.setIndex(materialList.size());
		material.registry = this;
		materialList.add(material);
	}

	@OnlyIn(value=Dist.CLIENT)
	public void init() {
	}

	//TODO: allow more block types
	public void registerOres(ItemGroup tab) {
		BlockOre metalBlocks;

		List<AllowedProducts> allAllowedProducts = AllowedProducts.getAllAllowedProducts();

		oreProducts = new HashMap<AllowedProducts, HashMap<zmaster587.libVulpes.api.material.Material, Item>> ();

		for(int i = 0; i < allAllowedProducts.size(); i++) {

			HashMap<zmaster587.libVulpes.api.material.Material, Item> productToItem = new HashMap<zmaster587.libVulpes.api.material.Material, Item>();
			oreProducts.put(AllowedProducts.getAllAllowedProducts().get(i), productToItem);

			AllowedProducts product = allAllowedProducts.get(i);
			for( int j = 0; j < materialList.size(); j++)
			{
				zmaster587.libVulpes.api.material.Material mat = materialList.get(j);

				if(!product.isOfType(mat.getAllowedProducts()))
					continue;


				String productName = product.name().toLowerCase(Locale.ENGLISH);
				Item.Properties itemProps = new Item.Properties();
				itemProps.group(tab);
				Item item;

				// if it's a block, register the block
				if(allAllowedProducts.get(i).isBlock())
				{
					Properties oreProperties = Properties.create(Material.ROCK).hardnessAndResistance(4f);
					oreProperties.harvestTool(mat.getTool());
					oreProperties.harvestLevel(mat.getHarvestLevel());
					metalBlocks = new BlockOre(oreProperties, product, mat);
					metalBlocks.setRegistryName(productName + mat.unlocalizedName);

					LibVulpesBlocks.registerBlock(metalBlocks);

					getBlockListForProduct(allAllowedProducts.get(i)).add(metalBlocks);

					item = new ItemMaterialBlock(metalBlocks, itemProps, mat);

					productToItem.put(mat, item);
					LibVulpesBlocks.registerItem(item.setRegistryName(productName + mat.unlocalizedName));
					// Register block tags
					for(int g = 0; g < mat.getOreDictNames().length; g++) {
						String str = mat.getOreDictNames()[g];
						BlockTags.getCollection().getTagByID(new ResourceLocation("forge:" + productName +  "s/" + str)).contains(metalBlocks);
					}
					BlockTags.getCollection().getTagByID(new ResourceLocation(LibVulpes.MODID,"block" + productName)).contains(metalBlocks);
				}
				else
				{
					item = new ItemOreProduct(itemProps, mat);

					productToItem.put(mat, item);
					LibVulpesBlocks.registerItem(item.setRegistryName(productName + mat.unlocalizedName));
				}

				// Register item tags
				for(int g = 0; g < mat.getOreDictNames().length; g++) {
					String str = mat.getOreDictNames()[g];
					ItemTags.getCollection().getTagByID(new ResourceLocation("forge:" + productName +  "s/" + str)).contains(item);
				}
			}
		}
	}

	public List<Block> getBlockListForProduct(AllowedProducts product) {
		return productBlockListMapping.get(product);
	}

	public Block getBlockForProduct(AllowedProducts product, zmaster587.libVulpes.api.material.Material material, int index) {

		for(Block block : productBlockListMapping.get(product))
		{
			if(((BlockOre)block).material == material)
				return block;
		}
		return null;
	}

	/**
	 * @param stack the item stack to get the material of
	 * @return {@link zmaster587.libVulpes.api.material.Material} of the itemstack if it exists, otherwise null
	 */
	public static zmaster587.libVulpes.api.material.Material getMaterialFromItemStack(@Nonnull ItemStack stack) {
		Item item = stack.getItem();

		//If items is an itemOreProduct it must have been registered

		for(MaterialRegistry  registry : registries) {

			Collection<ResourceLocation> tags = ItemTags.getCollection().getOwningTags(stack.getItem());

			//Check all ores against list
			for(zmaster587.libVulpes.api.material.Material ore : registry.materialList) {
				for(String str : ore.getOreDictNames()) {
					for(ResourceLocation i : tags) {

						if(i.getPath().contains(str)) 
							return ore;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param material
	 * @param product
	 * @return an ItemStack of size one containing the product with the given material, or ItemStack.EMPTY if one does not exist
	 */
	@Nonnull
	public static ItemStack getItemStackFromMaterialAndType(String material, AllowedProducts product) {
		return getItemStackFromMaterialAndType(material, product,1);
	}

	/**
	 * @param ore
	 * @param product
	 * @param amount stackSize
	 * @return an ItemStack of stackSize amount containing the product with the given material, or ItemStack.EMPTY if one does not exist
	 */
	@Nonnull
	public static ItemStack getItemStackFromMaterialAndType(String ore, AllowedProducts product, int amount) {
		for(MaterialRegistry  registry : registries) {
			zmaster587.libVulpes.api.material.Material ore2 = registry.strToMaterial.get(ore);

			if(registry.oreProducts.containsKey(product))
				if(registry.oreProducts.get(product).containsKey(ore2))
					return new ItemStack(registry.oreProducts.get(product).get(ore2), amount);
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Registers a mixed material or allow to automate recipe registration
	 * @param material new mixed material to create
	 */
	public static void registerMixedMaterial(MixedMaterial material) {
		Object inputObj = material.getInput();
		if(inputObj instanceof ItemStack && !((ItemStack) inputObj).isEmpty())
			mixedMaterialList.put( new ItemStackMapping((ItemStack) inputObj), material);
		else
			mixedMaterialList.put(inputObj, material);
	}

	/**
	 * @param stack
	 * @return {@link MixedMaterial} that makes up the item, null if the item is not registered
	 */
	public MixedMaterial getMixedMaterial(@Nonnull ItemStack stack) {
		return mixedMaterialList.get(new ItemStackMapping(stack));
	}

	/**
	 * @param str
	 * @return mixed material corresponding to the supplied string example: "bronze"
	 */
	public MixedMaterial getMixedMaterial(String str) {
		return mixedMaterialList.get(str);
	}

	public static int getColorFromItemMaterial(@Nonnull ItemStack stack) {

		zmaster587.libVulpes.api.material.Material material = getMaterialFromItemStack(stack);
		if(material == null) {
			return 0x7a7a7a;
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

	public static zmaster587.libVulpes.api.material.Material getMaterialFromName(
			String string) {
		for(MaterialRegistry registry : registries) {
			zmaster587.libVulpes.api.material.Material  material = registry.strToMaterial.get(string.toLowerCase());

			if(material != null)
				return material;
		}
		return null;
	}

	public static List<zmaster587.libVulpes.api.material.Material> getAllMaterials() {
		List<zmaster587.libVulpes.api.material.Material> list = new LinkedList<>();
		for(MaterialRegistry registry : registries) {
			list.addAll(registry.materialList);
		}

		return list;
	}


}
