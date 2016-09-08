package zmaster587.libVulpes.entity.fx;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FxErrorBlock extends EntityFX {
	
	public static final ResourceLocation icon = new ResourceLocation("libvulpes:textures/fx/x.png");
	
	public FxErrorBlock(World world, double x,
			double y, double z) {
		super(world, x, y, z);
		this.prevPosX = this.posX = this.lastTickPosX = x + 0.5;
		this.prevPosY = this.posY = this.lastTickPosY = y + 0.5;
		this.prevPosZ = this.posZ = this.lastTickPosZ = z + 0.5;

		this.particleMaxAge = 100;
	}
	
	@Override
	public void renderParticle(Tessellator tess, float x1,
			float y1, float z1, float x2,
			float y2, float z2) {
		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
		tess.draw();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		// Draw the box
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x1 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x1 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x1 - interpPosZ);
        float f10 = 0.25F * this.particleScale;
        
        tess.startDrawingQuads();
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1f);
        tess.addVertexWithUV((double)(f11 - y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 - x2 * f10 - z2 * f10), 1, 1);
        tess.addVertexWithUV((double)(f11 - y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 - x2 * f10 + z2 * f10), 1, 0);
        tess.addVertexWithUV((double)(f11 + y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 + x2 * f10 + z2 * f10), 0, 0);
        tess.addVertexWithUV((double)(f11 + y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 + x2 * f10 - z2 * f10), 0, 1);
        tess.draw();
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
		tess.startDrawingQuads();
	}
	
	

	@Override
	public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
	}
	
}
