package zmaster587.libVulpes.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderState.WriteMaskState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class RenderHelper {

	protected static final RenderState.TransparencyState LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("lightning_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	protected static final RenderState.WriteMaskState COLOR_ONLY = new WriteMaskState(true, false);

	protected static final RenderState.TargetState TRANSLUCENT_TARGET = new RenderState.TargetState("translucent_target", () -> {
		if (Minecraft.func_238218_y_()) {
			Minecraft.getInstance().worldRenderer.func_239228_q_().bindFramebuffer(false);
		}

	}, () -> {
		if (Minecraft.func_238218_y_()) {
			Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
		}

	});

	protected static final RenderState.ShadeModelState SHADE_DISABLED = new RenderState.ShadeModelState(false);
	protected static final RenderState.ShadeModelState SHADE_ENABLED = new RenderState.ShadeModelState(true);

	private static final RenderType SEMI_TRANSLUCENT = RenderType.makeType("coloredTranslucent", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.getBuilder().writeMask(COLOR_ONLY).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).shadeModel(SHADE_DISABLED).build(false));


	public static final RenderType getSolidEntityModelRenderType(ResourceLocation tex)
	{
		return RenderType.getEntitySolid(tex);
	}

	public static final RenderType getTranslucentEntityModelRenderType(ResourceLocation tex)
	{
		return RenderType.getEntityTranslucent(tex);
	}

	public static final RenderType getTranslucentManualRenderType()
	{
		return SEMI_TRANSLUCENT;
	}

	public static final RenderType getSolidManualRenderType()
	{
		return SEMI_TRANSLUCENT;
	}

	public static RenderType getTranslucentTexturedManualRenderType(ResourceLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	public static RenderType getSolidTexturedManualRenderType(ResourceLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	public static RenderType getTranslucentColoredManualRenderType(ResourceLocation location, float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		return null;
	}

	public static RenderType getLaserBeamType() {
		//pos_col
		/*GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		matrix.push();
		GlStateManager.color4f(0.2F, 0.2F, 0.2F, 0.3F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);*/
		return null;
	}

	public static RenderType getTranslucentBlock()
	{
		return RenderType.getTranslucent();
	}

	/**
	 * 
	 * @param text text to render
	 * @param horizontalLetter max letters in horizontal direction
	 * @param vertLetters max letters in vertial direction
	 * @param font resource location of the font to use
	 */
	/*public static void renderText(TextPart text, int horizontalLetter, int vertLetters, ResourceLocation font) {

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
	 * /
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
    }*/

	public static void renderTag(MatrixStack mat, IRenderTypeBuffer buffer, double distanceSq, String displayString, int packedLightIn, int sizeOnScreen) {
		renderTag(mat, buffer, distanceSq, displayString, packedLightIn, 1);
	}

	public static void renderTag(MatrixStack matrix, IRenderTypeBuffer buffer, double distanceSq, String displayString, int packedLightIn, float scale) {
		double d3 = distanceSq;

		Minecraft mc = Minecraft.getInstance();
		EntityRendererManager renderManager = mc.getRenderManager();

		if (!(distanceSq > 4096.0D)) {
			matrix.push();
			matrix.rotate(renderManager.getCameraOrientation());
			float f = 1.6F*scale;
			float f2 = 0.016666668F * f;
			matrix.scale(-f2, -f2, f2);
			Matrix4f matrix4f = matrix.getLast().getMatrix();
			float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int)(f1 * 255.0F) << 24;
			FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
			float f3 = (float)(-fontrenderer.func_238414_a_(new StringTextComponent(displayString)) / 2);
			fontrenderer.func_243247_a(new StringTextComponent(displayString), f3, (float)0, 553648127, false, matrix4f, buffer, false, j, packedLightIn);

			matrix.pop();
		}
	}

	/*public static void setupPlayerFacingMatrix(double distanceSq, double x, double y, double z) {

		Minecraft mc = Minecraft.getInstance();
		EntityRendererManager renderManager = mc.getRenderManager();
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.0F, (float)y + 0.5F, (float)z);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f1, -f1, f1);
	}*/

	public static void cleanupPlayerFacingMatrix() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public static void renderBlockWithEndPointers(IVertexBuilder buff, double radius, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
		double buffer;
		renderBottomFaceEndpoints(buff, radius, x1, y1 - radius/2d, z1, x2, y2 - radius/2d, z2, r,g,b,a);
		renderTopFaceEndpoints(buff, radius, x1, y1 + radius/2d, z1, x2, y2 + radius/2d, z2, r,g,b,a);
		renderNorthFaceEndpoints(buff, radius, x1, y1, z1 + radius/2d, x2, y2, z2 + radius/2d, r,g,b,a);
		renderSouthFaceEndpoints(buff, radius, x1, y1, z1 - radius/2d, x2, y2, z2 - radius/2d, r,g,b,a);
		renderEastFaceEndpoints(buff, radius, x1 + radius/2d, y1, z1, x2 + radius/2d, y2, z2, r,g,b,a);
		renderWestFaceEndpoints(buff, radius, x1 - radius/2d, y1, z1, x2 - radius/2d, y2, z2, r,g,b,a);
	}

	public static void renderCrossXZ(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		renderTopFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax, r,g,b,a);
		renderBottomFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax, r,g,b,a);
		renderNorthFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax, r,g,b,a);
		renderSouthFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax, r,g,b,a);
	}

	public static void renderTopFace(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax) 
	{
		renderTopFace(buff, yMax, xMin, zMin, xMax, zMax,1,1,1,1);
	}
	public static void renderTopFace(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, float r, float g, float b, float a) {
		//top
		buff.pos(xMin, yMax, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMin).color(a,g,b,a).endVertex();

	}

	public static void renderTopFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//top
		buff.pos(xMin, yMin, zMin - width).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin, zMin + width).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax + width).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax - width).color(a,g,b,a).endVertex();

	}

	public static void renderBottomFace(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, float r, float g, float b, float a) {
		//bottom
		buff.pos(xMax, yMax, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderBottomFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//top
		buff.pos(xMin, yMin, zMin + width).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin, zMin - width).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax - width).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax + width).color(a,g,b,a).endVertex();
	}	

	public static void renderNorthFace(IVertexBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, float r, float g, float b, float a) {
		//north
		buff.pos(xMin, yMax, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderNorthFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//north
		buff.pos(xMin, yMin + width, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax + width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin - width, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderSouthFace(IVertexBuilder buff, double zMax, double xMin, double yMin, double xMax, double yMax, float r, float g, float b, float a) {
		//south
		buff.pos(xMin, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax).color(a,g,b,a).endVertex();
	}

	public static void renderSouthFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//south
		buff.pos(xMin, yMin + width, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin - width, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax + width, zMax).color(a,g,b,a).endVertex();
	}

	public static void renderEastFace(IVertexBuilder buff, double xMax, double yMin, double zMin, double yMax, double zMax, float r, float g, float b, float a) {
		//east
		buff.pos(xMax, yMax, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin, zMin).color(a,g,b,a).endVertex();
	}


	public static void renderEastFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//east
		buff.pos(xMax, yMax + width, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMax - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMax, yMin + width, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderWestFace(IVertexBuilder buff, double xMin, double yMin, double zMin, double yMax, double zMax, float r, float g, float b, float a) {
		//west
		buff.pos(xMin, yMin, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderWestFaceEndpoints(IVertexBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		//west
		buff.pos(xMin, yMin + width, zMin).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMin - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax - width, zMax).color(a,g,b,a).endVertex();
		buff.pos(xMin, yMax + width, zMin).color(a,g,b,a).endVertex();
	}

	public static void renderTopFaceWithUV(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, float uMin, float uMax, float vMin, float vMax)
	{
		renderTopFaceWithUV(buff, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax, 1,1,1,1);
	}
	public static void renderTopFaceWithUV(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//top
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();


	}

	public static void renderBottomFaceWithUV(IVertexBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//bottom

		buff.pos(xMax, yMax, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();

	}

	public static void renderNorthFaceWithUV(IVertexBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, float uMin, float uMax, float vMin, float vMax)
	{
		renderNorthFaceWithUV(buff, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
	}
	
	public static void renderNorthFaceWithUV(IVertexBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//north
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
	}

	public static void renderNorthFaceWithUVNoNormal(IVertexBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//north
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
	}

	public static void renderSouthFaceWithUV(IVertexBuilder buff, double zMax, double xMin, double yMin, double xMax, double yMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//south
		buff.pos(xMin, yMax, zMax).tex(uMin, vMin).endVertex();
		buff.pos(xMin, yMin, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMax, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMin).endVertex();
	}

	public static void renderEastFaceWithUV(IVertexBuilder buff, double xMax, double yMin, double zMin, double yMax, double zMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//east
		buff.pos(xMax, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMin, vMax).endVertex();
	}


	public static void renderWestFaceWithUV(IVertexBuilder buff, double xMin, double yMin, double zMin, double yMax, double zMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {
		//west
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
		buff.pos(xMin, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMax, vMin).endVertex();
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
	}

	public static void renderCubeWithUV(IVertexBuilder buff, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float uMin, float uMax, float vMin, float vMax, float r, float g, float b, float a) {


		renderTopFaceWithUV(buff, yMax, xMin, zMin, xMax, zMax, uMin, uMax,vMin, vMax, r,g,b,a);
		renderNorthFaceWithUV(buff, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax, r,g,b,a);
		renderSouthFaceWithUV(buff, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax, r,g,b,a);
		renderEastFaceWithUV(buff, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax, r,g,b,a);
		renderWestFaceWithUV(buff, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax, r,g,b,a);
		renderBottomFaceWithUV(buff, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax, r,g,b,a);
	}

	public static void renderCube(IVertexBuilder buff, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, float r, float g, float b, float a) {
		renderTopFace(buff, yMax, xMin, zMin, xMax, zMax,r,g,b,a);
		renderNorthFace(buff, zMin, xMin, yMin, xMax, yMax,r,g,b,a);
		renderSouthFace(buff, zMax, xMin, yMin, xMax, yMax,r,g,b,a);
		renderEastFace(buff, xMax, yMin, zMin, yMax, zMax,r,g,b,a);
		renderWestFace(buff, xMin, yMin, zMin, yMax, zMax,r,g,b,a);
		renderBottomFace(buff, yMin, xMin, zMin, xMax, zMax,r,g,b,a);
	}

	/*public static void renderItem(TileEntity tile, ItemStack itemstack, ItemRenderer dummyItem)
	{
		if (itemstack != null)
		{
			ItemEntity entityitem = new ItemEntity(tile.getWorld(), 0.0D, 0.0D, 0.0D, itemstack);
			Item item = entityitem.getItem().getItem();
			entityitem.getItem().setCount(1);
			entityitem.hoverStart = 0.0F;
			GlStateManager.disableLighting();



			GlStateManager.scale(0.5F, 0.5F, 0.5F);

			if (!dummyItem.shouldRenderItemIn3D(entityitem.getItem()))
			{
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.pushAttrib();
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			dummyItem.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();



			GlStateManager.enableLighting();
		}
	}

	public static void renderItem(TileEntity tile, ItemEntity entityitem, ItemRenderer dummyItem)
	{
		GlStateManager.disableLighting();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (!dummyItem.shouldRenderItemIn3D(entityitem.getItem()))
		{
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.pushAttrib();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		dummyItem.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();



		GlStateManager.enableLighting();

	}*/
}
