package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
public class ModuleRadioButton  extends ModuleBase {

	IToggleButton tile;
	List<ModuleToggleSwitch> buttons;
	int previousSelection;
	int enabledColor, disabledColor;
	boolean enabled = true;

	public ModuleRadioButton(IToggleButton tile, List<ModuleToggleSwitch> buttons) {
		super(0, 0);
		this.buttons = buttons;
		this.tile = tile;

		enabledColor = 0xFF22FF22;
		disabledColor = 0xFFFF2222;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getOptionSelected() {
		for(int i = 0; i < buttons.size(); i++){
			if(buttons.get(i).getState())
				return i;
		}

		return -1;
	}

	public void setOptionSelected(int option) {
		for(int i = 0; i < buttons.size(); i++){
			if(i == option) {
				buttons.get(i).setToggleState(true);
				buttons.get(i).setColor(enabledColor);
			}
			else {
				buttons.get(i).setToggleState(false);
				buttons.get(i).setColor(disabledColor);
			}
		}
	}

	@Override
	public int numberOfChangesToSend() {
		return 1;
	}

	
	@Override
	public boolean needsUpdate(int localId) {
		return previousSelection != getOptionSelected();
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		previousSelection = getOptionSelected();
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		crafter.sendWindowProperty(container, variableId, getOptionSelected());
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		setOptionSelected(value);
	}

	@Override
	public List<Button> addButtons(int x, int y) {

		List<Button> buttonList = super.addButtons(x, y);

		for(ModuleToggleSwitch button : buttons) {
			buttonList.addAll(button.addButtons(x, y));
		}

		return buttonList;
	}

	@Override
	public void actionPerform(Button buttonObj) {

		if(enabled) {
			super.actionPerform(buttonObj);

			boolean isNotOurRadio = true;

			for(ModuleToggleSwitch button : buttons) {
				if(button.isButton(buttonObj)) {
					isNotOurRadio = false;
					break;
				}
			}

			if(isNotOurRadio)
				return;

			for(ModuleToggleSwitch button : buttons) {
				button.setToggleState(button.isButton(buttonObj));
				if(button.isButton(buttonObj))
					tile.onInventoryButtonPressed(button.buttonId);
			}
		}
	}

	@Override
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			ContainerScreen<? extends Container> gui, FontRenderer font) {
		super.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		for(ModuleToggleSwitch button : buttons) {
			button.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);
		}
	}

	@Override
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY, 
			FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);

		for(ModuleToggleSwitch button : buttons) {
			button.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		}
	}

}
