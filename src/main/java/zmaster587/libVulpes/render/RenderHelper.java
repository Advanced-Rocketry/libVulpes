package zmaster587.libVulpes.render;

import java.util.StringTokenizer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderHelper {
	
	/**
	 * 
	 * @param text text to render
	 * @param horizontalLetter max letters in horizontal direction
	 * @param vertLetters max letters in vertial direction
	 * @param font resource location of the font to use
	 */
	public static void renderText(TextPart text, int horizontalLetter, int vertLetters, ResourceLocation font) {
		
		StringTokenizer tokens = new StringTokenizer(text.text, "\n");
		Tessellator tess = Tessellator.instance;
		
		//tess.setColorRGBA(text.colorRGBA & 255, (text.colorRGBA << 8) & 255, (text.colorRGBA << 16) & 255, (text.colorRGBA << 24) & 255);
		
		GL11.glColor3ub((byte)((text.colorRGBA >> 16) & 255), (byte)((text.colorRGBA >> 8) & 255), (byte)(text.colorRGBA & 255));//, (byte)((text.colorRGBA >> 24) & 255));
		
		
		tess.startDrawingQuads();
		for(int line = 0; line < vertLetters && tokens.hasMoreTokens(); line++) {
			String token = tokens.nextToken();
			
			for(int i = 0 ; i < token.length() && i < horizontalLetter; i++) {
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY -text.size*line - text.size, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line - text.size, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY - text.size*line, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D,(8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);
				
			}
		}
		tess.draw();
	}
	
    /**
     * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b, a
     */
    public static boolean renderStandardBlockWithColorMultiplier(Block p_147736_1_, int p_147736_2_, int p_147736_3_, int p_147736_4_, float p_147736_5_, float p_147736_6_, float p_147736_7_, float alpha)
    {
    	RenderBlocks renderBlocks = RenderBlocks.getInstance();
    	renderBlocks.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (p_147736_1_ != Blocks.grass)
        {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_);

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_, 0))
        {
            tessellator.setBrightness(renderBlocks.renderMinY > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_));
            tessellator.setColorRGBA_F(f10, f13, f16, alpha);
            renderBlocks.renderFaceYNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 0));
            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_, 1))
        {
            tessellator.setBrightness(renderBlocks.renderMaxY < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_));
            tessellator.setColorRGBA_F(f7, f8, f9,alpha);
            renderBlocks.renderFaceYPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1, 2))
        {
            tessellator.setBrightness(renderBlocks.renderMinZ > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1));
            tessellator.setColorRGBA_F(f11, f14, f17, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 2);
            renderBlocks.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_, alpha);
                renderBlocks.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1, 3))
        {
            tessellator.setBrightness(renderBlocks.renderMaxZ < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1));
            tessellator.setColorRGBA_F(f11, f14, f17, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 3);
            renderBlocks.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_,alpha);
                renderBlocks.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_, 4))
        {
            tessellator.setBrightness(renderBlocks.renderMinX > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_));
            tessellator.setColorRGBA_F(f12, f15, f18,alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 4);
            renderBlocks.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_, alpha);
                renderBlocks.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_, 5))
        {
            tessellator.setBrightness(renderBlocks.renderMaxX < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_));
            tessellator.setColorRGBA_F(f12, f15, f18, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 5);
            renderBlocks.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_,alpha);
                renderBlocks.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }
	
	public static void renderTag(double distanceSq, String displayString, double x, double y, double z, int sizeOnScreen) {
        double d3 = distanceSq;

        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = RenderManager.instance;
        if (d3 <= (double)(sizeOnScreen * sizeOnScreen))
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.0F, (float)y + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;
            byte b0 = 0;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int j = fontrenderer.getStringWidth(displayString) / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawString(displayString, -fontrenderer.getStringWidth(displayString) / 2, b0, 553648127);
           
            GL11.glDepthMask(true);
            fontrenderer.drawString(displayString, -fontrenderer.getStringWidth(displayString) / 2, b0, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
	}
	
	public static void renderTopFace(Tessellator tess, double yMax, double xMin, double zMin, double xMax, double zMax) {
		//top
		tess.addVertex(xMin, yMax, zMin);
		tess.addVertex(xMin, yMax, zMax);
		tess.addVertex(xMax, yMax, zMax);
		tess.addVertex(xMax, yMax, zMin);
		
	}
	
	public static void renderBottomFace(Tessellator tess, double yMax, double xMin, double zMin, double xMax, double zMax) {
		//bottom
		tess.addVertex(xMax, yMax, zMin);
		tess.addVertex(xMax, yMax, zMax);
		tess.addVertex(xMin, yMax, zMax);
		tess.addVertex(xMin, yMax, zMin);
		
	}
	
	public static void renderNorthFace(Tessellator tess, double zMin, double xMin, double yMin, double xMax, double yMax) {
		//north
		tess.addVertex(xMin, yMax, zMin);
		tess.addVertex(xMax, yMax, zMin);
		tess.addVertex(xMax, yMin, zMin);
		tess.addVertex(xMin, yMin, zMin);
	}
	
	public static void renderSouthFace(Tessellator tess, double zMax, double xMin, double yMin, double xMax, double yMax) {
		//south
		tess.addVertex(xMin, yMax, zMax);
		tess.addVertex(xMin, yMin, zMax);
		tess.addVertex(xMax, yMin, zMax);
		tess.addVertex(xMax, yMax, zMax);
	}
		
	public static void renderEastFace(Tessellator tess, double xMax, double yMin, double zMin, double yMax, double zMax) {
		//east
		tess.addVertex(xMax, yMax, zMin);
		tess.addVertex(xMax, yMax, zMax);
		tess.addVertex(xMax, yMin, zMax);
		tess.addVertex(xMax, yMin, zMin);
	}
	
	
	public static void renderWestFace(Tessellator tess, double xMin, double yMin, double zMin, double yMax, double zMax) {
		//west
		tess.addVertex(xMin, yMin, zMin);
		tess.addVertex(xMin, yMin, zMax);
		tess.addVertex(xMin, yMax, zMax);
		tess.addVertex(xMin, yMax, zMin);
	}
	
	public static void renderTopFaceWithUV(Tessellator tess, double yMax, double xMin, double zMin, double xMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//top
		tess.addVertexWithUV(xMin, yMax, zMin, uMin, vMin);
		tess.addVertexWithUV(xMin, yMax, zMax, uMin, vMax);
		tess.addVertexWithUV(xMax, yMax, zMax, uMax, vMax);
		tess.addVertexWithUV(xMax, yMax, zMin, uMax, vMin);
		
	}
	
	public static void renderBottomFaceWithUV(Tessellator tess, double yMax, double xMin, double zMin, double xMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//bottom
		tess.addVertexWithUV(xMax, yMax, zMin, uMin, vMax);
		tess.addVertexWithUV(xMax, yMax, zMax, uMax, vMax);
		tess.addVertexWithUV(xMin, yMax, zMax, uMax, vMin);
		tess.addVertexWithUV(xMin, yMax, zMin, uMin, vMin);
		
	}
	
	public static void renderNorthFaceWithUV(Tessellator tess, double zMin, double xMin, double yMin, double xMax, double yMax, double uMin, double uMax, double vMin, double vMax) {
		//north
		tess.addVertexWithUV(xMin, yMax, zMin, uMin, vMin);
		tess.addVertexWithUV(xMax, yMax, zMin, uMax, vMin);
		tess.addVertexWithUV(xMax, yMin, zMin, uMax, vMax);
		tess.addVertexWithUV(xMin, yMin, zMin, uMin, vMax);
	}
	
	public static void renderSouthFaceWithUV(Tessellator tess, double zMax, double xMin, double yMin, double xMax, double yMax, double uMin, double uMax, double vMin, double vMax) {
		//south
		tess.addVertexWithUV(xMin, yMax, zMax, uMin, vMin);
		tess.addVertexWithUV(xMin, yMin, zMax, uMin, vMax);
		tess.addVertexWithUV(xMax, yMin, zMax, uMax, vMax);
		tess.addVertexWithUV(xMax, yMax, zMax, uMax, vMin);
	}
		
	public static void renderEastFaceWithUV(Tessellator tess, double xMax, double yMin, double zMin, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//east
		tess.addVertexWithUV(xMax, yMax, zMin, uMin, vMin);
		tess.addVertexWithUV(xMax, yMax, zMax, uMax, vMin);
		tess.addVertexWithUV(xMax, yMin, zMax, uMax, vMax);
		tess.addVertexWithUV(xMax, yMin, zMin, uMin, vMax);
	}
	
	
	public static void renderWestFaceWithUV(Tessellator tess, double xMin, double yMin, double zMin, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//west
		tess.addVertexWithUV(xMin, yMin, zMin, uMin, vMax);
		tess.addVertexWithUV(xMin, yMin, zMax, uMax, vMax);
		tess.addVertexWithUV(xMin, yMax, zMax, uMax, vMin);
		tess.addVertexWithUV(xMin, yMax, zMin, uMin, vMin);
	}
	
	public static void renderCubeWithUV(Tessellator tess, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		
		
		renderTopFaceWithUV(tess, yMax, xMin, zMin, xMax, zMax, uMin, uMax,vMin, vMax);
		renderNorthFaceWithUV(tess, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
		renderSouthFaceWithUV(tess, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
		renderEastFaceWithUV(tess, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
		renderWestFaceWithUV(tess, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
		renderBottomFaceWithUV(tess, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
	}
}
