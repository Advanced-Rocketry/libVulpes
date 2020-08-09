package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.client.util.ProgressBarImage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleDualProgressBar extends ModuleProgress {

	private float multiplier;
	
	public ModuleDualProgressBar(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, IProgressBar progress) {
		super(offsetX, offsetY, id, progressBar, progress);
		multiplier =1f;
	}
	
	public ModuleDualProgressBar(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, IProgressBar progress, String tooltip) {
		super(offsetX, offsetY, id, progressBar, progress, tooltip);
		multiplier =1f;
	}
	
	
	public void setTooltipValueMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	protected List<String> getToolTip() {
		List<String> modifiedList = new LinkedList<String>();
		
		for(String string : tooltip) {
			int centerPoint = progress.getTotalProgress(id);
			int variation = progress.getProgress(id);
			
			String newStr = string.replaceAll("%b", String.format("%.2f",multiplier*MathHelper.clamp(centerPoint - variation/2,0,100)));
			newStr = newStr.replaceAll("%a", String.format("%.2f",multiplier*MathHelper.clamp(centerPoint + variation/2,0,100)));
			modifiedList.add(newStr);
		}
		return modifiedList;
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		
		float totalProgress = progress.getTotalProgress(id)/100f;
		float randomnessFactor = progress.getProgress(id)/100f;
		
		progressBar.renderProgressBarPartial(mat, x + offsetX, y + offsetY, totalProgress , 0.5f*randomnessFactor, gui);
	}
}
