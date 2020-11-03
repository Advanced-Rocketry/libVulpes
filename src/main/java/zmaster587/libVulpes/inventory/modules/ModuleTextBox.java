package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.inventory.GuiModular;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class ModuleTextBox extends ModuleBase {

	// renderButton drawTextBox
	// charTyped textboxKeyTyped
	@OnlyIn(value=Dist.CLIENT)
	TextFieldWidget textBox;
	String currentText;
	IGuiCallback tile;

	public ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, int sizeX, int sizeY, int maxStrLen) {
		super(offsetX, offsetY);
		this.tile = tile;
		if(EffectiveSide.get().isClient()) {
			textBox =  new TextFieldWidget(Minecraft.getInstance().fontRenderer, offsetX, offsetY, sizeX, sizeY, new StringTextComponent(""));
			textBox.setCanLoseFocus(true);
			textBox.setFocused2(false);
			textBox.setEnabled(true);
			textBox.setMaxStringLength(maxStrLen);
			textBox.setEnableBackgroundDrawing(true);
		}
		currentText = "";
	}

	protected ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, String initialString) {
		super(offsetX, offsetY);

		this.tile = tile;
		currentText = initialString;
	}

	@Override
	public boolean keyTyped(int chr, int t) {
		// Isfocused
		if(textBox.isFocused()) {

			if(GLFW.GLFW_KEY_ESCAPE == t) {
				textBox.setFocused2(false);
			}
			else {
				textBox.charTyped((char) chr, t);
				textBox.keyPressed((char) chr, t, 0);

				//Make callback to calling tile
				tile.onModuleUpdated(this);
				return false;
			}
		}

		return true;
	}


	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {

		//Make sure we can focus the textboxes
		if(offsetX < x && offsetY < y && offsetX + textBox.getAdjustedWidth() > x  && offsetY + textBox.getHeightRealms() > y )
			textBox.setFocused2(true);
		else
			textBox.setFocused2(false);

	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, ContainerScreen<? extends Container> gui, FontRenderer font) {
		super.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);

		textBox.renderButton(mat, guiOffsetX, guiOffsetY, zLevel);
	}

	public void setText(String str) {
		textBox.setText(str);
	}

	public String getText() {
		return textBox.getText();
	}

}
