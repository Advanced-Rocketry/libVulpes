package zmaster587.libVulpes.inventory.modules;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;

public class ModuleText extends ModuleBase {

	List<String> text;
	int color;
	boolean centered;
	float scale;
	boolean alwaysOnTop;

	public ModuleText(int offsetX, int offsetY, String text, int color) {
		super(offsetX, offsetY);

		this.text = new ArrayList<String>();
		scale = 1f;
		setText(text);
		this.color = color;
		centered = false;
		alwaysOnTop = false;
	}

	public ModuleText(int offsetX, int offsetY, String text, int color, float scale) {
		this(offsetX, offsetY, text, color);
		this.scale = scale;
	}

	public ModuleText(int offsetX, int offsetY, String text, int color, boolean centered) {
		this(offsetX, offsetY, text, color);
		this.centered = centered;
		scale = 1f;
	}

	public void setText(String text) {

		this.text.clear();
		for(String str : text.split("\\n")) {
			this.text.add(str);
		}
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public String getText() {

		String str = "";

		for(String str2 : this.text) {
			str += "\n" + str2;
		}

		return str.substring(1);
	}

	@Override
	public void renderForeground(MatrixStack buf, int x, int y, int mouseX,
			int mouseY, float zLevel, ContainerScreen<? extends Container> gui, FontRenderer font) {

	}

	@Override
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack mat, int x, int y, int mouseX, int mouseY, FontRenderer font) {

		GL11.glPushMatrix();
		if(alwaysOnTop)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glScalef(scale, scale, scale);
		for(int i = 0; i < text.size(); i++) {
			if(centered)
				font.func_238421_b_(mat, text.get(i), (x + offsetX - (font.getStringWidth(text.get(i))/2)), y + offsetY + i*font.FONT_HEIGHT, color);
			else
				font.func_238421_b_(mat, text.get(i),(int)((x + offsetX)/scale), (int)((y + offsetY + i*font.FONT_HEIGHT)/scale), color);
		}
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		
		if(alwaysOnTop)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}
}
