package zmaster587.libVulpes.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScrollHelper {
	
	double scrollDelta;
	
	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void mouseEvent(GuiScreenEvent.MouseScrollEvent.Pre event) {
		this.scrollDelta = event.getScrollDelta();
	}
	
	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void resetTick(TickEvent.ClientTickEvent event) {
		this.scrollDelta = 0;
	}
	
	public double getScrollDelta()
	{
		return scrollDelta;
	}
}
