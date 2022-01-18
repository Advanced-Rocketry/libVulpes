package zmaster587.libVulpes.inventory.modules;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import zmaster587.libVulpes.gui.GuiImageButton;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ModuleButton extends ModuleBase {

	@OnlyIn(value=Dist.CLIENT)
	public GuiImageButton button;

	IButtonInventory tile;

	protected int color;

	protected int bgColor;
	protected String text, tooltipText;
	ResourceLocation[] buttonImages;
	protected boolean visible = true;

	boolean enabled = true;
	protected String sound;
	Object additionalData;

	public ModuleButton(int offsetX, int offsetY, String text, IButtonInventory tile, ResourceLocation[] buttonImages) {
		super(offsetX, offsetY);
		this.tile = tile;
		this.buttonImages = buttonImages;
		this.text = text;

		sound = "";
		sizeX = 52;
		sizeY = 16;

		bgColor = 0xFFFFFFFF;
		color = 0xFF22FF22; // Lime green
	}

	public ModuleButton(int offsetX, int offsetY, String text, IButtonInventory tile, ResourceLocation[] buttonImages, String tooltipText) {
		this(offsetX, offsetY, text, tile, buttonImages);
		this.tooltipText = tooltipText;
	}


	public ModuleButton(int offsetX, int offsetY, String text, IButtonInventory tile, ResourceLocation[] buttonImages, int sizeX, int sizeY) {
		this(offsetX, offsetY, text, tile, buttonImages);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public ModuleButton(int offsetX, int offsetY, String text, IButtonInventory tile, ResourceLocation[] buttonImages, String tooltipText, int sizeX, int sizeY) {
		this(offsetX, offsetY, text, tile, buttonImages,sizeX, sizeY);
		this.tooltipText = tooltipText;
	}

	public ModuleButton setAdditionalData(Object data) {
		additionalData = data;
		return this;
	}

	public Object getAdditionalData() {
		return additionalData;
	}

	public void setImage(ResourceLocation[] images) {
		button.setButtonTexture(images);
	}


	public void setSound(String str) {
		if(EffectiveSide.get().isClient())
			if(button == null)
				sound = str;
			else
				button.setSound(str);
	}

	/**
	 * Sets the text of this button
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	public void setToolTipText(String text){
		tooltipText = text;
	}

	/**
	 * @return the text displayed on the button
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param color sets the color of the button ARGB8
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * 
	 * @return the color of this button ARGB8
	 */
	public int getColor() {
		return this.color;
	}

	public void setBGColor(int color) {

		this.bgColor = color;
		if(button != null)
			button.setBackgroundColor(this.bgColor);
	}

	/**
	 * Sets the button to be (in)visible
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if(button != null)
			button.visible = visible;
	}

	/**
	 * 
	 * @return true if the button can be rendered
	 */
	public boolean isVisible() {
		return button.visible;
	}

	/**
	 * dis/enables the button
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(button != null)
			button.enabled = enabled;
	}

	/**
	 * 
	 * @return true if the button is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@OnlyIn(value=Dist.CLIENT)
	public List<AbstractButton> addButtons(int x, int y) {

		List<AbstractButton> list = new LinkedList<>();

		button = new GuiImageButton(x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);

		button.visible = visible;
		button.enabled = enabled;

		if(!sound.isEmpty()) {
			button.setSound(sound);
			sound = "";
		}

		button.setBackgroundColor(bgColor);
		list.add(button);

		return list;
	}

	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(AbstractButton button) {
		if(enabled && button != null && button == this.button) {
			tile.onInventoryButtonPressed(this);
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;

		return enabled && relativeX > 0 && relativeX < sizeX && relativeY > 0 && relativeY < sizeY;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, ContainerScreen<? extends Container> gui, FontRenderer font) {

		if(visible) {

			// RenderCenteredString
			AbstractGui.drawCenteredString(mat, font, text, offsetX + sizeX / 2, offsetY + sizeY / 2  - font.FONT_HEIGHT/2, color);

			if(tooltipText != null) {

				if( isMouseOver(mouseX, mouseY) ) {
					List<String> list = new LinkedList<>();
					Collections.addAll(list, tooltipText.split("\n"));
					this.drawTooltip(gui, mat, list, mouseX, mouseY, zLevel, font);
				}
			}
		}


	}
}
