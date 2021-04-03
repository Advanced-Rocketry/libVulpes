package zmaster587.libVulpes.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiItemToggleButton extends GuiToggleButtonImage {

	ItemStack stack;
	
	public GuiItemToggleButton(int x, int y, int width, int height, ResourceLocation[] location, ItemStack stack) {
		super(x, y, width, height, location);
		this.stack = stack;
	}
	
	@Override
	public void render(MatrixStack matrix, int par2, int par3, float p_230431_4_)
	{
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			
			//Only display the hover icon if a pressed icon is found and the mouse is hovered
			if(hovered && (buttonTexture.length > 2 && buttonTexture[2] != null ))
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[1]);
			else if(state && ( buttonTexture.length > 2 && buttonTexture[2] != null ))
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[2]);
			else if(!state)
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[0]);
			else // if !state and button[2] == null
				Minecraft.getInstance().getTextureManager().bindTexture(buttonTexture[1]);
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);


			//Draw the button...each button should contain 3 images default state, hover, and pressed
			
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
           
			
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        // height == zlevel
	        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        vertexbuffer.pos(x, y + height, (double)this.height).tex(0, 1).endVertex();
	        vertexbuffer.pos(x + width, y + height, (double)this.height).tex( 1, 1).endVertex();
	        vertexbuffer.pos(x + width, y, (double)this.height).tex(1, 0).endVertex();
	        vertexbuffer.pos(x, y, (double)this.height).tex(0, 0).endVertex();
	        tessellator.draw();
			
			// mousedragged
			//this.render(matrix, (int) par2, (int) par3, 0);
		}
	}
}