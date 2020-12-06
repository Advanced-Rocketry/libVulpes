package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.glfw.GLFW;

public class ModuleNumericTextbox extends ModuleTextBox {

	public ModuleNumericTextbox(IGuiCallback tile, int offsetX, int offsetY,
			int sizeX, int sizeY, int maxStrLen) {
		super(tile, offsetX, offsetY, sizeX, sizeY, maxStrLen);
	}

	public ModuleNumericTextbox(IGuiCallback tile, int offsetX, int offsetY, String initialString) {
		super(tile, offsetX, offsetY, initialString);
	}

	@Override
	public boolean keyTyped(int chr, int t) {

		// is focused
		if(textBox.isFocused()) {
			if(GLFW.GLFW_KEY_ESCAPE == chr)
				textBox.setFocused2(false);
			else if(Character.isDigit(chr) || chr == '-' || chr == GLFW.GLFW_KEY_BACKSPACE || chr == GLFW.GLFW_KEY_DELETE || chr == GLFW.GLFW_KEY_LEFT || chr == GLFW.GLFW_KEY_RIGHT) {
				if((chr != '-' || (textBox.getCursorPosition() == 0 && !textBox.getText().startsWith("-")))) {
					if(Character.isDigit(chr) || chr == '-')
						textBox.charTyped((char) chr, t);
					else
						textBox.keyPressed((char) chr, t, 0);

					//Make callback to calling tile
					
					tile.onModuleUpdated(this);
					return false;
				}
			}
		}

		return true;
	}
}
