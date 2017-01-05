package zmaster587.libVulpes.client;

import zmaster587.libVulpes.api.IToggleableMachine;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RepeatingSound extends MovingSound {

	TileEntity tile;
	IToggleableMachine toggle;

	public RepeatingSound(SoundEvent soundIn, SoundCategory categoryIn, TileEntity tile) {
		super(soundIn, categoryIn);
		this.tile = tile;
		this.repeat = true;
		xPosF = tile.getPos().getX();
		yPosF = tile.getPos().getY();
		zPosF = tile.getPos().getZ();
		
		if(tile instanceof IToggleableMachine)
			toggle = (IToggleableMachine)tile;
	}

	@Override
	public void update() {
		if(tile.isInvalid())
			this.donePlaying = true;
		if(toggle != null)
			this.volume = toggle.isRunning() ? 1f : 0f;
	}

}
