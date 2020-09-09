package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.IconResource;

@OnlyIn(value=Dist.CLIENT)
public class ModuleImage extends ModuleBase {

	protected IconResource icon;

	public ModuleImage(int offsetX, int offsetY, IconResource icon) {
		super(offsetX, offsetY);
		this.icon = icon;
		
		if(icon != null) {
			this.sizeX = icon.getxSize();
			this.sizeY = icon.getySize();
		}
	}
	
	public ModuleImage(int offsetX, int offsetY, ResourceLocation icon, int sizeX, int sizeY) {
		super(offsetX, offsetY);
		this.icon = new IconResource(-1, -1, sizeX, sizeY, icon);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);

		if(isEnabled()) {
			GL11.glEnable(GL11.GL_BLEND);
			Minecraft.getInstance().getTextureManager().bindTexture(icon.getResourceLocation());
			if(icon.getxLoc() == -1) {
				Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				RenderHelper.renderNorthFaceWithUV(mat, Tessellator.getInstance().getBuffer(), 0, x + offsetX, y + offsetY, x + offsetX + icon.getxSize(), y + offsetY + icon.getySize(), 0, 1, 0, 1, 1,1,1,1);
				Tessellator.getInstance().draw();
			} else
				gui.func_238474_b_(mat, x + offsetX, y + offsetY, icon.getxLoc(), icon.getyLoc(), icon.getxSize(), icon.getySize());
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
