package zmaster587.libVulpes.client;

import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.common.CommonProxy;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	@Override
	public String getLocalizedString(String str) {
		return I18n.format(str);
	}
	
	@Override
	public void registerRenderers() {
		RendererModelBlock blockRenderer = new RendererModelBlock();
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LibVulpesBlocks.blockMotor), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LibVulpesBlocks.blockAdvancedMotor), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LibVulpesBlocks.blockEnhancedMotor), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LibVulpesBlocks.blockEliteMotor), blockRenderer);
		
	}
	
	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(LibVulpesItems.itemHoloProjector);
	}
	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {

		if(particle == "errorBox") {
			FxErrorBlock fx = new FxErrorBlock(world, x, y, z);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
	
	@Override
	public void playSound(Object resource) {
		Minecraft.getMinecraft().getSoundHandler().playSound((ISound)resource);
	}
}
