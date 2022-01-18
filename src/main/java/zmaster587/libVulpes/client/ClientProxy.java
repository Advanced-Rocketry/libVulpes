package zmaster587.libVulpes.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.util.ScrollHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	ScrollHelper scrollHelper;
	
	public ClientProxy() {
		scrollHelper = new ScrollHelper();
	}
	
	@Override
	public String getLocalizedString(String str) {
		return I18n.format(str);
	}
	
	@Override
	public void init() {
		LibVulpes.materialRegistry.init();

		RenderTypeLookup.setRenderLayer(LibVulpesBlocks.blockMotor, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(LibVulpesBlocks.blockAdvancedMotor, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(LibVulpesBlocks.blockEnhancedMotor, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(LibVulpesBlocks.blockEliteMotor, RenderType.getCutout());
	}
	
	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(LibVulpesItems.itemHoloProjector);
		MinecraftForge.EVENT_BUS.register(scrollHelper);
	}
	
	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		if(particle == "errorBox" && world.isRemote) {
			FxErrorBlock fx = new FxErrorBlock((ClientWorld) world, x, y, z);
			Minecraft.getInstance().particles.addEffect(fx);
		}
	}
	
	@Override
	public void playSound(Object sound) {
		if(sound instanceof ITickableSound) {
			ITickableSound sound2 = (ITickableSound)sound;
			//if(sound2.isDonePlaying())
				Minecraft.getInstance().getSoundHandler().play(sound2);
		}
	}
	
	@Override
	public void playSound(World worldObj, BlockPos pos, SoundEvent event, SoundCategory cat, float volume, float pitch) {
		worldObj.playSound(Minecraft.getInstance().player, pos, event, SoundCategory.BLOCKS, Minecraft.getInstance().gameSettings.getSoundLevel(cat)*volume,  0.975f + worldObj.rand.nextFloat()*0.05f);
		
	}
	
	@Override
	public double getScrollDelta()
	{
		return scrollHelper.getScrollDelta();
	}
}
