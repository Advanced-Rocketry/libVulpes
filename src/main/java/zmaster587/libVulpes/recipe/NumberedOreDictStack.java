package zmaster587.libVulpes.recipe;

public class NumberedOreDictStack {

	String ore;
	int number;
	
	public NumberedOreDictStack(String ore, int number) {
		this.ore = ore;
		this.number = number;
	}
	
	public String getOre() {
		return ore;
	}
	
	public int getNumber() {
		return number;
	}
}
