package zmaster587.libVulpes.items;

import zmaster587.libVulpes.api.material.Material;
import net.minecraft.item.Item;

public class ItemOreProduct extends Item {

	public Material material;

	public ItemOreProduct(Item.Properties properties, Material mat) {
		super(properties);

		material = mat;
	}
}
