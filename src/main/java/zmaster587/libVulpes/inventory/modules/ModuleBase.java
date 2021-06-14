package zmaster587.libVulpes.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.GuiModular;

import java.util.LinkedList;
import java.util.List;

public abstract class ModuleBase {

	//Gui render offsets
	public int offsetX;
	public int offsetY;
	protected int sizeX;
	protected int sizeY;
	//List of slots contained by this module
	protected List<Slot> slotList;
	//Because each player has it's own instance of the container, in order to send changes to all clients we need to make sure we're running the same tick when calling "isUpdateRequired"
	protected Long lastTickTime;
	//is True for the tick all players are being updated
	boolean isSendingChanges = false;
	
	private boolean enabled;
	private boolean visible;

	protected ModuleBase(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		slotList = new LinkedList<>();
		enabled = true;
		visible = true;
	}

	protected long getCurrentTime() {
		return LibVulpes.time;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}

	/**
	 * Called when is it time to change the previous state used to check if an update is needed
	 * @param localId
	 */
	protected void updatePreviousState(int localId) {

	}

	/**
	 * Determines whether or not an update is needed to be sent for this variable.
	 * check
	 * @param localId
	 * @return whether or not the variable associated with this local id needs an update
	 */
	public boolean needsUpdate(int localId) {
		return false;
	}
	
	public void setEnabled(boolean state) {
		enabled = state;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	@SideOnly(Side.CLIENT)
	public void onMouseClicked(GuiModular gui, int x, int y, int button) {
		
	}
	
	@SideOnly(Side.CLIENT)
	public void onMouseClickedAndDragged(int x, int y, int button, long timeSineLastClick) {
		
	}
	
	/**
	 * @param chr char typed
	 * @param t
	 * @return true to allow allow keybinds to be passed on, false to suppress keybinds
	 */
	@SideOnly(Side.CLIENT)
	public boolean keyTyped(char chr, int t) {
		return true;
	}
	
	/**
	 * 
	 * @param localId The id requested local to this module eg (0 to numberOfChangesToSend())
	 * @return true if the object at this id
	 */
	public boolean isUpdateRequired(int localId) {

		boolean ret = needsUpdate(localId);

		if(ret) { //if this variable is ready to be updated, set the current tick time and set changes are being sent
			if(!this.isSendingChanges) {
				this.lastTickTime = getCurrentTime();
				this.isSendingChanges = true;
			} else if(/* isSendingChanges && (inferred)*/ this.lastTickTime != getCurrentTime()) {
				this.isSendingChanges = false;
				updatePreviousState(localId);
			}
		}

		return ret;
	}

	/**
	 * Called when the background is rendered on the client
	 * @param gui gui calling the render
	 * @param x x offset of the top left corner of the container
	 * @param y y offset of the top left corner of the container
	 * @param font FontRenderer, passed on the off-chance text needs to be rendered
	 */
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {
		gui.mc.getTextureManager().bindTexture(CommonResources.genericBackground);
		for(Slot slot : slotList) {
			gui.drawTexturedModalRect(x + slot.xPos - 1, y + slot.yPos - 1, 176, 0, 18, 18);
		}
	}

	/**
	 * @param guiOffsetX location of the gui on the X access
	 * @param guiOffsetY location of the gui on the Y access
	 * @param mouseX x location of the mouse relative to the window
	 * @param mouseY y location of the mouse relative to the window
	 * @param zLevel zLevel of the gui
	 * @param gui gui calling this method
	 * @param font FontRenderer, passed on the off-chance text needs to be rendered
	 */
	@SideOnly(Side.CLIENT)
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {
	}

	/**
	 * @param container container called this method
	 * @param crafter crafter to send the changes to
	 * @param variableId container id to send
	 * @param localId id of the object, scoped to this module
	 */
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {

	}

	/**
	 * 
	 * @param container container calling this method
	 * @param crafter crafter to send the information to
	 * @param variableId non-scoped id of the object to send
	 */
	public void sendInitialChanges(Container container, IContainerListener crafter, int variableId) {
		for(int i = 0; i < numberOfChangesToSend(); i++) {
			sendChanges(container, crafter, variableId + i, i);
		}
	}

	/**
	 * @param slot scoped id of the object updated
	 * @param value value recieved from the server
	 */
	public void onChangeRecieved(int slot, int value) {

	}

	/**
	 * @return the number of objects this module can update
	 */
	public int numberOfChangesToSend() {
		return 0;
	}

	/**
	 * @param x x offset of the gui
	 * @param y y offset of the gui
	 * @return list of buttons associated with this module
	 */
	@SideOnly(Side.CLIENT)
	public List<GuiButton> addButtons(int x, int y) {
		return new LinkedList<>();
	}

	/**
	 * Called when a button is clicked
	 * @param button GuiButton that was clicked
	 */
	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {

	}

	/**
	 * @return List of slots to add to this module
	 */
	public List<Slot> getSlots(Container container) {
		return new LinkedList<>();
	}

	/**
	 * @param gui gui to draw on
	 * @param textList List of strings to draw as the tooltip
	 * @param x x position to draw the tooltip
	 * @param y y position to draw the tooltip
	 * @param zLevel zLevel of the gui
	 * @param font fontrender
	 */
	@SideOnly(Side.CLIENT)
	protected void drawTooltip(GuiContainer gui, List<String> textList , int x, int y, float zLevel, FontRenderer font) {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int k = 0;

		for (String s : textList) {
			int l = font.getStringWidth(s);

			if (l > k) {
				k = l;
			}
		}

		int j2 = x + 12;
		int k2 = y - 12;
		int i1 = 8 + 12*(textList.size()-1);

		zLevel = 300.0F;

		int j1 = -267386864;
		drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1,zLevel);
		drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1,zLevel);
		drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1,zLevel);
		drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1,zLevel);
		drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1,zLevel);
		int k1 = 1347420415;
		int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
		drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1,zLevel);
		drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1,zLevel);
		drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1,zLevel);
		drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1,zLevel);

		for (int i2 = 0; i2 < textList.size(); ++i2)
		{
			String s1 = textList.get(i2);
			font.drawStringWithShadow(s1, j2, k2, -1);

			if (i2 == 0)
			{
				k2 += 2;
			}

			k2 += 10;
		}

		zLevel = 0.0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);

	}

	/**
	 * Draws a gradient Rectangle
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param colorA
	 * @param colorB
	 * @param zLevel
	 */
	@SideOnly(Side.CLIENT)
	protected void drawGradientRect(int x1, int y1, int x2, int y2, int colorA, int colorB, float zLevel)
	{
		float f = (float)(colorA >> 24 & 255) / 255.0F;
		float f1 = (float)(colorA >> 16 & 255) / 255.0F;
		float f2 = (float)(colorA >> 8 & 255) / 255.0F;
		float f3 = (float)(colorA & 255) / 255.0F;
		float f4 = (float)(colorB >> 24 & 255) / 255.0F;
		float f5 = (float)(colorB >> 16 & 255) / 255.0F;
		float f6 = (float)(colorB >> 8 & 255) / 255.0F;
		float f7 = (float)(colorB & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertex = tessellator.getBuffer();
		vertex.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		vertex.pos(x2, y1, zLevel).color(f1, f2, f3, f).endVertex();
		vertex.pos(x1, y1, zLevel).color(f1, f2, f3, f).endVertex();
		vertex.pos(x1, y2, zLevel).color(f5, f6, f7, f4).endVertex();
		vertex.pos(x2, y2, zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
