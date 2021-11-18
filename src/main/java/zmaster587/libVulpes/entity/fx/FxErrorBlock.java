package zmaster587.libVulpes.entity.fx;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.render.RenderHelper;

public class FxErrorBlock extends SpriteTexturedParticle {

	public static final ResourceLocation icon = new ResourceLocation("libvulpes:textures/fx/x.png");
	private static final RenderType XRENDER = RenderHelper.getParticleType(icon);

	public FxErrorBlock(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
		this.prevPosX = this.posX = x + 0.5;
		this.prevPosY = this.posY = y + 0.5;
		this.prevPosZ = this.posZ = z + 0.5;
		this.maxAge = 100;
	}


	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
	      Vector3d vector3d = renderInfo.getProjectedView();
	      float f = (float)(MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - vector3d.getX());
	      float f1 = (float)(MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - vector3d.getY());
	      float f2 = (float)(MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - vector3d.getZ());
	      Quaternion quaternion;
	      if (this.particleAngle == 0.0F) {
	         quaternion = renderInfo.getRotation();
	      } else {
	         quaternion = new Quaternion(renderInfo.getRotation());
	         float f3 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
	         quaternion.multiply(Vector3f.ZP.rotation(f3));
	      }

	      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
	      vector3f1.transform(quaternion);
	      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
	      float f4 = this.getScale(partialTicks);

	      for(int i = 0; i < 4; ++i) {
	         Vector3f vector3f = avector3f[i];
	         vector3f.transform(quaternion);
	         vector3f.mul(f4);
	         vector3f.add(f, f1, f2);
	      }

	      
	      float f7 = 0;//this.getMinU();
	      float f8 = 1;//this.getMaxU();
	      float f5 = 0;//this.getMinV();
	      float f6 = 1;//this.getMaxV();
	      int j = this.getBrightnessForRender(partialTicks);
	      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
	      
	      IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(XRENDER);
	      ivertexbuilder.pos(avector3f[0].getX(), avector3f[0].getY(), avector3f[0].getZ()).tex(f8, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
	      ivertexbuilder.pos(avector3f[1].getX(), avector3f[1].getY(), avector3f[1].getZ()).tex(f8, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
	      ivertexbuilder.pos(avector3f[2].getX(), avector3f[2].getY(), avector3f[2].getZ()).tex(f7, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
	      ivertexbuilder.pos(avector3f[3].getX(), avector3f[3].getY(), avector3f[3].getZ()).tex(f7, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
	      
	      irendertypebuffer$impl.finish();
	}
	
	@Override
	public void tick() {
		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}
	}
	
   @OnlyIn(value=Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {

      
      public Factory() {
      }
      
      @Override
      public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new FxErrorBlock(worldIn, x, y, z);
      }
   }

}
