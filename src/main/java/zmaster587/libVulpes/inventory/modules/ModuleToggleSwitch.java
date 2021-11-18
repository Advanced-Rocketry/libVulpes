package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.gui.GuiToggleButtonImage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleToggleSwitch extends ModuleButton {


	boolean currentState, prevState;
	IToggleButton tile;
	boolean enabled = true;


	public ModuleToggleSwitch(int offsetX, int offsetY, String text, IToggleButton tile, ResourceLocation[] buttonImages, boolean defaultState) {
		super(offsetX, offsetY, text, tile, buttonImages);
		this.tile = tile;
		currentState = defaultState;
	}

	public ModuleToggleSwitch(int offsetX, int offsetY, String text, IToggleButton tile, ResourceLocation[] buttonImages, String tooltipText, boolean defaultState) {
		super(offsetX, offsetY, text, tile, buttonImages, tooltipText);
		this.tile = tile;
		currentState = defaultState;
	}

	public ModuleToggleSwitch(int offsetX, int offsetY, String text, IToggleButton tile, ResourceLocation[] buttonImages, int sizeX, int sizeY, boolean defaultState) {
		super(offsetX, offsetY, text, tile, buttonImages, sizeX, sizeY);
		this.tile = tile;
		currentState = defaultState;
	}

	public ModuleToggleSwitch(int offsetX, int offsetY, String text, IToggleButton tile, ResourceLocation[] buttonImages, String tooltipText, int sizeX, int sizeY, boolean defaultState) {
		super(offsetX, offsetY, text, tile, buttonImages, tooltipText, sizeX, sizeY);
		this.tile = tile;
		currentState = defaultState;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	

	@OnlyIn(value=Dist.CLIENT)
	public List<Button> addButtons(int x, int y) {

		List<Button> list = new LinkedList<>();

		button = new GuiToggleButtonImage(x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);
		((GuiToggleButtonImage)button).setState(currentState);
		
		button.visible = visible;
		button.enabled = enabled;

		list.add(button);

		return list;
	}

	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(Button button) {
		if(enabled && button == this.button) {
			this.currentState = !this.currentState;
			this.tile.onInventoryButtonPressed(this);
		}
	}

	public void setToggleState(boolean state) {
		currentState = state;
	}
	
	public boolean getState() {
		return currentState;
	}
	
	@Override
	public boolean needsUpdate(int localId) {
		return prevState != currentState;
	}
	
	@Override
	public void onChangeRecieved(int slot, int value) {
		this.setToggleState(value == 1);
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		prevState = currentState;
	}
	
	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		crafter.sendWindowProperty(container, variableId, currentState ? 1 : 0);
	}
	
	public boolean isButton(Button button) {
		return button == this.button;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack mat, int x, int y, int mouseX, int mouseY, FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		((GuiToggleButtonImage)button).setState(currentState);
	}
}