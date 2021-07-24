package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleScaledImage extends ModuleBase {
	
	ResourceLocation icon;
	int sizeX, sizeY;
	float minX, maxX, minY, maxY;
	float alpha;
	
	public ModuleScaledImage(int locX, int locY, int sizeX, int sizeY, ResourceLocation icon) {
		super(locX, locY);
		this.icon = icon;
		
		if(sizeX < 0) {
			minX = 1f;
			maxX = 0f;
			sizeX = -sizeX;
		}
		else {
			minX = 0f;
			maxX = 1f;
		}
		
		if(sizeY < 0) {
			minY = 1f;
			maxY = 0f;
			sizeY = -sizeY;
		}
		else {
			minY = 0f;
			maxY = 1f;
		}
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		alpha = 1f;
	}

	public ModuleScaledImage(int locX, int locY, int sizeX, int sizeY, float alpha, ResourceLocation icon) {
		this(locX, locY, sizeX, sizeY, icon);
		this.alpha = alpha;
	}
	
	public void setResourceLocation(ResourceLocation location ) {
		this.icon = location;
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		
		if(alpha < 1f) {
			RenderSystem.color4f(alpha, alpha, alpha, alpha);
			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
			
		gui.getMinecraft().getTextureManager().bindTexture(icon);
		GlStateManager.color4f(alpha, alpha, alpha, alpha);
        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buff.pos(x + this.offsetX, y + this.offsetY + sizeY, 0).tex(minX, maxY).endVertex();
        buff.pos(x + this.offsetX + sizeX, y + this.offsetY + sizeY, 0).tex(maxX, maxY).endVertex();
        buff.pos(x + this.offsetX + sizeX, y + this.offsetY, 0).tex(maxX, minY).endVertex();
        buff.pos(x + this.offsetX, y + this.offsetY, 0).tex(minX, minY).endVertex();
        Tessellator.getInstance().draw();
        if(alpha < 1f) {
        	RenderSystem.color4f(1, 1, 1, 1);
        	RenderSystem.disableBlend();
			RenderSystem.enableAlphaTest();
        }
	}
}
