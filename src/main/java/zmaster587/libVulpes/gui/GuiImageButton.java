package zmaster587.libVulpes.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;

public class GuiImageButton extends Button {

	static class DummyPress implements IPressable
	{
		public DummyPress() {
		}
		
		@Override
		public void onPress(Button arg0) {
			// Do nothing here
		}
		
	}
	
	protected ResourceLocation[] buttonTexture;
	private String soundString;
	private int bgColor;
	
	//Workarounds
	public boolean visible;
	public boolean hovered;
	public boolean enabled;
	public int width, height;
	
	/**
	 * 
	 * @param id id of the button
	 * @param x x position of the button
	 * @param y y position of the button
	 * @param width width of the button in pixels
	 * @param height height of the button in pixels
	 * @param location index 0: default, index 1: hover, index 2: pressed, index 3: disabled
	 */
	public GuiImageButton(int x, int y, int width, int height, ResourceLocation[] location) {
		super(x, y, width, height, new StringTextComponent(""), new DummyPress());
		buttonTexture = location;
		soundString = "";
		bgColor = 0xFFFFFFFF;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
	
	// playPressSound
	@Override
	public void playDownSound(SoundHandler sound) {
		if(soundString.isEmpty())
			super.playDownSound(sound);
		else
			sound.play(SimpleSound.master(new SoundEvent(new ResourceLocation("advancedrocketry:" + soundString)), 1.0F));
	}

	public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
		//super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
	}
	
	//DrawButtonFG
	@Override
	public void render(MatrixStack matrix, int par2, int par3, float p_230431_4_) {
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			// get hover state
			int hoverState = this.getYImage(this.hovered);

			/*if(mousePressed(minecraft, par2, par3) && buttonTexture[2] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[2]);*/
			if(buttonTexture.length > 1 && hoverState == 2 && buttonTexture[1] != null)
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[1]);
			else if(buttonTexture.length > 2 && hoverState == 4 && buttonTexture[3] != null)
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[3]);
			else
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[0]);

			float r = (bgColor & 0xFF)/255f;
			float g = ((bgColor >>> 8) & 0xFF)/255f;
			float b = ((bgColor >>> 16) & 0xFF)/255f;
			float a = 1f;
			
			RenderSystem.color4f(r,g,b,a);


			//Draw the button...each button should contain 3 images default state, hover, and pressed

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
           
			
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        // height == zlevel
	        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	        vertexbuffer.pos(x, y + height, (double)this.height).tex(0, 1).color(r, g, b, a).endVertex();
	        vertexbuffer.pos(x + width, y + height, (double)this.height).tex( 1, 1).color(r, g, b, a).endVertex();
	        vertexbuffer.pos(x + width, y, (double)this.height).tex(1, 0).color(r, g, b, a).endVertex();
	        vertexbuffer.pos(x, y, (double)this.height).tex(0, 0).color(r, g, b, a).endVertex();
	        tessellator.draw();
			
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

			RenderSystem.disableBlend();
			// mousedragged
			//super.render(matrix, (int) par2, (int) par3, 0);
		}
	}
}
