package zmaster587.libVulpes.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;

public class GuiImageButton extends Button {

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
		super(x, y, width, height, new StringTextComponent(""), null);
		buttonTexture = location;
		soundString = "";
		bgColor = 0xFFFFFFFF;
		this.field_230690_l_ = x;
		this.field_230691_m_ = y;
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
	public void func_230988_a_(SoundHandler sound) {
		if(soundString.isEmpty())
			super.func_230988_a_(sound);
		else
			sound.play(SimpleSound.master(new SoundEvent(new ResourceLocation("advancedrocketry:" + soundString)), 1.0F));
	}

	public void func_230431_b_(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
		//super.func_230431_b_(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
	}
	
	//DrawButtonFG
	@Override
	public void func_230430_a_(MatrixStack matrix, int par2, int par3, float p_230431_4_) {
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.field_230690_l_ && par3 >= this.field_230691_m_ && par2 < this.field_230690_l_ + this.width && par3 < this.field_230691_m_ + this.height;
			// get hover state
			int hoverState = this.func_230989_a_(this.hovered);

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
	        // field_230689_k_ == zlevel
	        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        vertexbuffer.pos(field_230690_l_, field_230691_m_ + height, (double)this.field_230689_k_).tex(0, 1).endVertex();
	        vertexbuffer.pos(field_230690_l_ + width, field_230691_m_ + height, (double)this.field_230689_k_).tex( 1, 1).endVertex();
	        vertexbuffer.pos(field_230690_l_ + width, field_230691_m_, (double)this.field_230689_k_).tex(1, 0).endVertex();
	        vertexbuffer.pos(field_230690_l_, field_230691_m_, (double)this.field_230689_k_).tex(0, 0).endVertex();
	        tessellator.draw();
			
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

			RenderSystem.disableBlend();
			// mousedragged
			//super.func_230430_a_(matrix, (int) par2, (int) par3, 0);
		}
	}
}
