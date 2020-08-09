package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.GuiModular;
import net.minecraft.util.math.MathHelper;

public class ModuleSlider extends ModuleProgress {

	public ModuleSlider(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, ISliderBar progress) {
		super(offsetX, offsetY, id, progressBar, progress);
	}
	
	@Override
	public void onMouseClickedAndDragged(double x, double y, int button) {
		onMouseClicked(null,x, y, button);
	}
	
	@Override
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {

		if(button == 0 && isEnabled()) {
			double localX = x - offsetX - progressBar.getInsetX();
			double localY = y - offsetY - progressBar.getInsetY();

			//If user is over the slider
			if(localX > 0 && localX < progressBar.getBackWidth() - progressBar.getInsetX() && localY > 0 && localY < progressBar.getBackHeight() - progressBar.getInsetY()) {

				double percent;
				if(progressBar.getDirection().getXOffset() != 0) { // horizontal
					percent = MathHelper.clamp((localX  + progressBar.getInsetX())/ ((float)(progressBar.getBackWidth() - 2*progressBar.getInsetX())),0f,1f);
				}
				else if(progressBar.getDirection().getYOffset() == 1)
					percent = 1 - (localY / (float)(progressBar.getBackHeight() - progressBar.getInsetY()));
				else
					percent = localY / (float)(progressBar.getBackHeight() + progressBar.getInsetY());
				
				
				((ISliderBar)progress).setProgressByUser(id, (int) (percent*progress.getTotalProgress(id)));
			}
		}
	}
}