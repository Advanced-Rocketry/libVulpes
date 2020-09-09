package zmaster587.libVulpes.recipe;

import net.minecraft.util.ResourceLocation;

public class NumberedOreDictStack {

	ResourceLocation ore;
	int number;
	
	public NumberedOreDictStack(ResourceLocation ore, int number) {
		this.ore = ore;
		this.number = number;
	}
	
	public ResourceLocation getOre() {
		return ore;
	}
	
	public int getNumber() {
		return number;
	}
	
	@Override
	public String toString() {
		return ore.toString() + "x" + number;
	}
}
