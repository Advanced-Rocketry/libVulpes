package zmaster587.libVulpes.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
/**
 * Used to display an arrow or some other image moving along a bar
 *
 */
public class IndicatorBarImage extends ProgressBarImage {

	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight, int insetX, int insetY, Direction direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, insetX, insetY, direction,image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight,Direction direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, 0, 0, direction, image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, Direction direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, backWidth, backHeight, 0, 0, direction, image);
	}
	
	@Override
	public void renderProgressBar(MatrixStack mat, int x, int y, float percent, net.minecraft.client.gui.AbstractGui gui) {
		
		Minecraft.getInstance().getTextureManager().bindTexture(image);
		
		gui.blit(mat, x, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		int xProgress, yProgress;
		
		if(direction == Direction.WEST)
			xProgress = (int) (backWidth - insetX - ( ( backWidth - ( insetX*2 ) )*percent)) - foreHeight/2;
		else if(direction == Direction.EAST)
			xProgress = (int) (insetX - foreWidth + ( ( backWidth - ( insetX*2 ) )*percent));
		else
			xProgress = insetX;
		
		if(direction == Direction.UP)
			yProgress = (int) (backHeight - insetY - ( ( backHeight - ( insetY*2 ) )*percent)) - foreHeight/2;
		else if(direction == Direction.DOWN)
			yProgress = (int) (insetY + ( ( backHeight - ( insetY*2 ) )*percent)) + foreHeight/2;
		else
			yProgress = insetY;
		
		gui.blit(mat, x + xProgress, y + yProgress, foreOffsetX, foreOffsetY, foreWidth, foreHeight);
	}
}
