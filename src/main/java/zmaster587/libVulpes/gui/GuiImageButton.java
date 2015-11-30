package zmaster587.libVulpes.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiImageButton extends GuiButton {

	protected ResourceLocation[] buttonTexture;
	private String soundString;
	private int bgColor;
	/**
	 * 
	 * @param id id of the button
	 * @param x x position of the button
	 * @param y y position of the button
	 * @param width width of the button in pixels
	 * @param height height of the button in pixels
	 * @param location index 0: default, index 1: hover, index 2: pressed, index 3: disabled
	 */
	public GuiImageButton(int id, int x, int y, int width, int height, ResourceLocation[] location) {
		super(id, x, y, width, height,"");
		buttonTexture = location;
		soundString = "";
		bgColor = 0xFFFFFFFF;
		//TODO: add exception
	}

	public void setButtonTexture(ResourceLocation[] buttonTexture) {
		this.buttonTexture = buttonTexture;
	}


	public void setSound(String str) {
		soundString = str;
	}
	
	public void setBackgroundColor(int color) {
		bgColor = color;
	}

	@Override
	public void func_146113_a(SoundHandler sound) {
		if(soundString.isEmpty())
			super.func_146113_a(sound);
		else
			sound.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("advancedrocketry:" + soundString), 1.0F));
	}

	@Override
	public void drawButton(Minecraft minecraft, int par2, int par3)
	{
		if (this.visible)
		{
			//
			this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			int hoverState = this.getHoverState(this.field_146123_n);

			/*if(mousePressed(minecraft, par2, par3) && buttonTexture[2] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[2]);*/
			if(buttonTexture.length > 1 && hoverState == 2 && buttonTexture[1] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[1]);
			else if(buttonTexture.length > 2 && hoverState == 4 && buttonTexture[3] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[3]);
			else
				minecraft.getTextureManager().bindTexture(buttonTexture[0]);

			
			
			GL11.glColor4ub((byte)(bgColor & 0xFF), (byte)((bgColor >>> 8) & 0xFF), (byte)((bgColor >>> 16) & 0xFF), (byte)0xFF);


			//Draw the button...each button should contain 3 images default state, hover, and pressed

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(xPosition, yPosition + height, (double)this.zLevel, 0, 1);
			tessellator.addVertexWithUV(xPosition + width, yPosition + height, (double)this.zLevel, 1, 1);
			tessellator.addVertexWithUV(xPosition + width, yPosition, (double)this.zLevel, 1, 0);
			tessellator.addVertexWithUV(xPosition, yPosition, (double)this.zLevel, 0, 0);
			tessellator.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			this.mouseDragged(minecraft, par2, par3);
		}
	}
}
