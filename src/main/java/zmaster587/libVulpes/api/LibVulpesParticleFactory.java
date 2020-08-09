package zmaster587.libVulpes.api;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;

public class LibVulpesParticleFactory {

	public static ParticleType<BasicParticleType> FX_ERROR;
	
	public static void registerFactories()
	{
		FX_ERROR = Registry.register(Registry.PARTICLE_TYPE, "libvulpes:FxError", new BasicParticleType(true));
		
		Minecraft.getInstance().particles.registerFactory(FX_ERROR, new FxErrorBlock.Factory());
	}
}
