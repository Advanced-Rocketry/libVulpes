package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.input.Keyboard;

public class ModuleNumericTextbox extends ModuleTextBox {

	public ModuleNumericTextbox(IGuiCallback tile, int offsetX, int offsetY,
			int sizeX, int sizeY, int maxStrLen) {
		super(tile, offsetX, offsetY, sizeX, sizeY, maxStrLen);
	}

	public ModuleNumericTextbox(IGuiCallback tile, int offsetX, int offsetY, String initialString) {
		super(tile, offsetX, offsetY, initialString);
	}

	@Override
	public boolean keyTyped(char chr, int t) {

		if(textBox.isFocused()) {
			if(Keyboard.KEY_ESCAPE == t)
				textBox.setFocused(false);
			else if(Character.isDigit(chr) || chr == '-' || t == Keyboard.KEY_BACK || t == Keyboard.KEY_DELETE || t == Keyboard.KEY_LEFT || t == Keyboard.KEY_RIGHT) {
				if((chr != '-' || (textBox.getCursorPosition() == 0 && !textBox.getText().startsWith("-")))) {
					textBox.textboxKeyTyped(chr, t);

					//Make callback to calling tile
					tile.onModuleUpdated(this);
					return false;
				}
			}
		}

		return true;
	}
}
