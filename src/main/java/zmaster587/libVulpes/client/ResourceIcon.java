package zmaster587.libVulpes.client;

import net.minecraft.util.ResourceLocation;

public class ResourceIcon { //extends TextureAtlasSprite {

	ResourceLocation location;

	public ResourceIcon(ResourceLocation location, int imageSizeX, int imageSizeY, int offsetX, int offsetY, int sizeX, int sizeY) {
		//super("");
		/*this.location = location;
		this.width = sizeX;
		this.height = sizeY;
		initSprite( imageSizeX, imageSizeY, offsetX, offsetY, false);*/
	}
	
	public ResourceIcon(ResourceLocation location) {
		//super("");
		/*this.location = location;
		this.width = 1;
		this.height = 1;
		initSprite( 1, 1, 0, 0, false);*/
	}
	
	public ResourceLocation getResourceLocation() {
		return location;
	}
}
