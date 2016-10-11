package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.inventory.GuiModular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ModuleTextBox extends ModuleBase {

	@SideOnly(Side.CLIENT)
	GuiTextField textBox;
	String currentText;
	IGuiCallback tile;

	@SideOnly(Side.CLIENT)
	public ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, int sizeX, int sizeY, int maxStrLen) {
		super(offsetX, offsetY);
		this.tile = tile;
		textBox = new GuiTextField(Minecraft.getMinecraft().fontRenderer ,offsetX, offsetY, sizeX, sizeY);
		textBox.setCanLoseFocus(true);
		textBox.setFocused(false);
		textBox.setEnabled(true);
		textBox.setMaxStringLength(maxStrLen);
		textBox.setEnableBackgroundDrawing(true);
		currentText = "";
	}

	public ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, String initialString) {
		super(offsetX, offsetY);
		
		this.tile = tile;
		currentText = initialString;
	}

	@Override
	public void keyTyped(char chr, int t) {
		super.keyTyped(chr, t);
			if(textBox.isFocused()) {
				textBox.textboxKeyTyped(chr, t);
				
				//Make callback to calling tile
				tile.onModuleUpdated(this);
			}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(GuiModular gui, int x, int y, int button) {

		//Make sure we can focus the textboxes
		if(offsetX < x && offsetY < y && offsetX + textBox.width > x  && offsetY + textBox.height > y )
			textBox.setFocused(true);
		else
			textBox.setFocused(false);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);

		textBox.drawTextBox();
	}

	public void setText(String str) {
		textBox.setText(str);
	}
	
	public String getText() {
		return textBox.getText();
	}

}
