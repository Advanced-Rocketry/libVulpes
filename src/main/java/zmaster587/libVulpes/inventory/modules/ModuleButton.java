package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import zmaster587.libVulpes.gui.GuiImageButton;
import zmaster587.libVulpes.inventory.ContainerModular;

public class ModuleButton extends ModuleBase {

	@OnlyIn(value=Dist.CLIENT)
	public GuiImageButton button;

	IButtonInventory tile;
	boolean prevState;
	public int buttonId;

	protected int color;

	protected int bgColor;
	protected String text, tooltipText;
	ResourceLocation buttonImages[];
	protected boolean visible = true;

	boolean enabled = true;
	protected String sound;

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[]) {
		super(offsetX, offsetY);
		this.tile = tile;
		this.buttonImages = buttonImages;
		this.text = text;
		this.buttonId = buttonId;

		sound = "";
		sizeX = 52;
		sizeY = 16;

		bgColor = 0xFFFFFFFF;
		color = 0xFF22FF22; // Lime green
	}

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], String tooltipText) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages);
		this.tooltipText = tooltipText;
	}


	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], int sizeX, int sizeY) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], String tooltipText, int sizeX, int sizeY) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages,sizeX, sizeY);
		this.tooltipText = tooltipText;
	}

	/*public ModuleButtton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, IconResource buttonImage) {

		this(offsetX, offsetY, buttonId, text, tile, new Re);
		this.sizeX = buttonImage.getxSize();
		this.sizeY = buttonImage.getySize();
	}*/

	public void setImage(ResourceLocation[] images) {
		((GuiImageButton)button).setButtonTexture(images);
	}


	public void setSound(String str) {
		if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT)
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
			this.enabled = button.enabled = enabled;
	}

	/**
	 * 
	 * @return true if the button is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@OnlyIn(value=Dist.CLIENT)
	public List<Button> addButtons(int x, int y) {

		List<Button> list = new LinkedList<Button>();

		button = new GuiImageButton(buttonId, x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);

		button.visible = visible;

		if(!sound.isEmpty()) {
			button.setSound(sound);
			sound = "";
		}

		button.setBackgroundColor(bgColor);
		list.add(button);

		return list;
	}

	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(Button button) {
		if(enabled && button == this.button) {
			tile.onInventoryButtonPressed(buttonId);
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;

		return enabled && relativeX > 0 && relativeX < sizeX && relativeY > 0 && relativeY < sizeY;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			ContainerScreen<? extends Container> gui, FontRenderer font) {

		//if(visible) {
		
		// RenderCenteredString
		gui.func_238476_c_(mat, font, text, offsetX + sizeX / 2, offsetY + sizeY / 2  - font.FONT_HEIGHT/2, color);
		
		if(tooltipText != null) {

			if( isMouseOver(mouseX, mouseY) ) {
				List<String> list = new LinkedList<String>();
				for(String str : tooltipText.split("\n")) {

					list.add(str);

				}
				this.drawTooltip((ContainerScreen<Container>) gui, mat, list, mouseX, mouseY, zLevel, font);
			}
			//}
		}
	}
}
