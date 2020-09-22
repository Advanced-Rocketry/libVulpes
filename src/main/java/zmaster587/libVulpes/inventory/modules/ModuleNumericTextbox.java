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
		if(textBox.func_230999_j_()) {
			if(GLFW.GLFW_KEY_ESCAPE == chr)
				textBox.setFocused2(false);
			else if(Character.isDigit(chr) || chr == '-' || chr == GLFW.GLFW_KEY_BACKSPACE || chr == GLFW.GLFW_KEY_DELETE || chr == GLFW.GLFW_KEY_LEFT || chr == GLFW.GLFW_KEY_RIGHT) {
				if((chr != '-' || (textBox.getCursorPosition() == 0 && !textBox.getText().startsWith("-")))) {
					if(Character.isDigit(chr) || chr == '-')
						textBox.func_231042_a_((char) chr, t);
					else
						textBox.func_231046_a_((char) chr, t, 0);

					//Make callback to calling tile
					
					tile.onModuleUpdated(this);
					return false;
				}
			}
		}

		return true;
	}
}
