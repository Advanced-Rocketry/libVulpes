package zmaster587.libVulpes.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.api.IToggleableMachine;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RepeatingSound extends MovingSound {

	TileEntity tile;
	IToggleableMachine toggle;

	public RepeatingSound(ResourceLocation location, TileEntity tile) {
		super(location);
		this.tile = tile;
		this.repeat = true;
		xPosF = tile.xCoord;
		yPosF = tile.yCoord;
		zPosF = tile.zCoord;
		
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
