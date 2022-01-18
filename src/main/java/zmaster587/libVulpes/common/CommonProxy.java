package zmaster587.libVulpes.common;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonProxy {
	public String getLocalizedString(String str) {
		return str;
	}

	public void registerEventHandlers() { }

	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) { }

	public void init() { }

	public void playSound(World world, BlockPos pos, SoundEvent event, SoundCategory cat, float volume, float pitch) { }

	public void playSound(Object sound) { }

	public double getScrollDelta() {
		return 0;
	}
}
