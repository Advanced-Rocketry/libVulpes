package zmaster587.libVulpes.client;

import zmaster587.libVulpes.api.IToggleableMachine;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class RepeatingSound extends TickableSound {
	TileEntity tile;
	IToggleableMachine toggle;

	public RepeatingSound(SoundEvent soundIn, SoundCategory categoryIn, TileEntity tile) {
		super(soundIn, categoryIn);
		this.tile = tile;
		this.repeat = true;
		x = tile.getPos().getX();
		y = tile.getPos().getY();
		z = tile.getPos().getZ();
		
		if(tile instanceof IToggleableMachine)
			toggle = (IToggleableMachine)tile;
	}
	
	@Override
	public void tick() {
		if(tile.isRemoved())
			// done playing
			this.func_239509_o_();
		if(toggle != null)
			this.volume = toggle.isRunning() ? 1f : 0f;
	}

}
