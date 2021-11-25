package zmaster587.libVulpes.inventory;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.widget.button.AbstractButton;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiModular extends ContainerScreen<ContainerModular> {

	List<ModuleBase> modules;
	String unlocalizedName;
	boolean hasSlots;
	// width width
	// height height
	// buttons buttonList (tho now technically it's all wigets)
	// font fontrenderer
	// blit renderModelRect
	// itemRenderer.zlevel OR field_230662_a_ zlevel or getBlitOffset()
	// drawString func_243246_a w/ shadow

	public GuiModular(ContainerModular container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.modules = container.modules;
		unlocalizedName = title.getString();
		hasSlots = container.includePlayerInv;
	}

	/*@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
	}*/

	@Override
	public void init(Minecraft minecraft, int width, int height) {
		if(container.guid != guiId.MODULARFULLSCREEN)
		{
			super.init(minecraft, width, height);
			return;
		}

		this.minecraft = minecraft;
		this.itemRenderer = minecraft.getItemRenderer();
		this.font = minecraft.fontRenderer;
		//this.width = width;
		//this.height = height;
		java.util.function.Consumer<Widget> remove = (b) -> {
			buttons.remove(b);
			children.remove(b);
		};
		if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
			this.buttons.clear();
			this.children.clear();
			this.setListener(null);
			this.init();
		}
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
	}

	// InitGui
	@Override
	public void init() {
		if(container.guid == guiId.MODULARFULLSCREEN) {
			this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
			this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
			this.width = Minecraft.getInstance().getMainWindow().getWidth();
			this.height = Minecraft.getInstance().getMainWindow().getHeight();
		}

		super.init();

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		for(ModuleBase module : modules) {
			List<AbstractButton> buttonList = module.addButtons(x, y);
			if(!buttonList.isEmpty()) {
				this.buttons.addAll(buttonList);
			}
		}
	}

	// Action Performed
	@Override
	public void setListener(@Nullable IGuiEventListener button) {
		super.setListener(button);

		if(button == null)
			return;
		for(ModuleBase module : modules) {
			module.actionPerform((AbstractButton)button);
		}
	}


	//KeyTyped
	@Override
	protected boolean itemStackMoved(int keyCode, int scanCode)  {

		boolean superKeypress = true;

		for(ModuleBase module : modules) {
			if(superKeypress)
				superKeypress = module.keyTyped(keyCode, scanCode);
			else
				module.keyTyped(keyCode, scanCode);
		}


		if(superKeypress)
			return super.itemStackMoved(keyCode, scanCode);

		return true;
	}


	// Draw foreground
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int a, int b)  {
		//super.drawGuiContainerForegroundLayer(matrix, a, b);


		//renderString
		this.font.drawString(matrix, title.getString(), 8, 6, 0xffffff);
		matrix.push();
		matrix.translate(0, 0, 999);
		for(ModuleBase module : modules)
			if(module.getVisible())
				module.renderForeground(matrix, (width - xSize)/2, (height - ySize) / 2,a - (width - xSize)/2 ,b - (height - ySize) / 2, itemRenderer.zLevel, this, font);

		matrix.pop();
	}

	//onMouseclicked
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		boolean handled = super.mouseReleased(x, y, button);
		//Handles buttons (mostly vanilla copy)
		if(button == 0) {

			List<Widget> fullButtonList = new LinkedList<>(this.buttons);


			for(IGuiEventListener iguieventlistener : fullButtonList) {
				if (iguieventlistener.isMouseOver(x, y)) {
					AbstractButton button2 = (AbstractButton)iguieventlistener;
					button2.playDownSound(this.getMinecraft().getSoundHandler());
					this.setListener(button2);
				}
			}
		}

		for(ModuleBase module : modules)
			module.onMouseClicked(this, x - (width - xSize) / 2, y - (height - ySize) / 2, button);

		return handled;
	}

	// mouse click drag
	@Override
	public boolean mouseDragged(double x, double y, int button, double x2, double y2) {
		boolean handled = super.mouseDragged(x, y, button, x2, y2);

		for(ModuleBase module : modules)
			module.onMouseClickedAndDragged(x - (width - xSize) / 2, y - (height - ySize) / 2, button);

		return handled;
	}

	// Draw background
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float f1, int i2, int i3)
	{
		//Guarantee proper color
		GlStateManager.color4f(1, 1, 1, 1);

		Minecraft.getInstance().getTextureManager().bindTexture(CommonResources.genericBackground);

		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.blit(matrix,x, y, 0, 0, 176, 171);

		if(!hasSlots) {
			this.blit(matrix, x + 7, y + 88, 7, 12, 162, 54);
		}

		for(ModuleBase module : modules) {
			if(module.getVisible())
				module.renderBackground(this, matrix, x, y, i2, i3, font);
		}
	}

	public List<Rectangle> getExtraAreasCovered() {
		List<Rectangle> list = new LinkedList<>();

		for(ModuleBase module : modules) {
			list.add(new Rectangle((width - xSize) / 2 + module.offsetX, (height - ySize) / 2 + module.offsetY, module.getSizeX(), module.getSizeY()));

		}
		return list;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	//drawScreen
	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(matrix); // DrawDefaultWorldBackground
		super.render(matrix, mouseX, mouseY, partialTicks); //drawScreen
		this.renderHoveredTooltip(matrix, mouseX, mouseY); // renderHoveredToolTip
	}
}