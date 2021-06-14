package zmaster587.libVulpes.inventory.modules;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.client.util.ProgressBarImage;

import java.util.Arrays;
import java.util.List;

public class ModuleProgress extends ModuleBase {

	ProgressBarImage progressBar;
	IProgressBar progress;
	int prevProgress;
	int prevTotalProgress;
	int id;
	List<String> tooltip;

	public ModuleProgress(int offsetX, int offsetY, int id, ProgressBarImage progressBar, IProgressBar progress) {
		super(offsetX, offsetY);
		this.progressBar = progressBar;
		this.progress = progress;
		this.id = id;
	}

	public ModuleProgress(int offsetX, int offsetY, int id, ProgressBarImage progressBar, IProgressBar progress, String tooltip) {
		this(offsetX, offsetY, id, progressBar, progress);
		setTooltip(tooltip);
	}

	public void setTooltip(String tooltip) {

		if(tooltip == null || tooltip.isEmpty())
			this.tooltip.clear();
		else
			this.tooltip = Arrays.asList(tooltip.split("\\n"));
	}

	@Override
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		
		List<String> tooltip = getToolTip();
		if(tooltip != null && !tooltip.isEmpty()) {
			int localX = mouseX - offsetX - progressBar.getInsetX();
			int localY = mouseY - offsetY - progressBar.getInsetY();

			if(localX > 0 && localX < progressBar.getBackWidth() - progressBar.getInsetX() && localY > 0 && localY < progressBar.getBackHeight() - progressBar.getInsetY()) {
				drawTooltip(gui, tooltip, mouseX, MathHelper.clamp(mouseY, 16, Integer.MAX_VALUE), zLevel, font);
			}
		}
	}
	
	protected List<String> getToolTip() {
		return this.tooltip;
	}

	@Override
	public boolean needsUpdate(int localId) {
		switch(localId) {
		case 0:
			return prevProgress != progress.getProgress(id);
		case 1:
			return prevTotalProgress != progress.getTotalProgress(id);
		}
		return false;
	}

	@Override
	protected void updatePreviousState(int localId) {
		switch(localId) {
		case 0:
			prevProgress = progress.getProgress(id);
			break;
		case 1:
			prevTotalProgress = progress.getTotalProgress(id);
		}
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {
		switch(localId) {
		case 0:
			crafter.sendWindowProperty(container, variableId, progress.getProgress(id));
			break;
		case 1:
			crafter.sendWindowProperty(container, variableId, progress.getTotalProgress(id));
		}
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		switch(slot) {
		case 0:
			progress.setProgress(id, value);
			break;
		case 1:
			progress.setTotalProgress(id, value);
		}
	}

	@Override
	public int numberOfChangesToSend() {
		return 2;
	}

	protected float getProgress() {
		return progress.getNormallizedProgress(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY , FontRenderer font) {
		progressBar.renderProgressBar(x + offsetX, y + offsetY,getProgress(), gui);
	}
}
