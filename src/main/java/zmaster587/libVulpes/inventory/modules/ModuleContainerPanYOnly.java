package zmaster587.libVulpes.inventory.modules;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiModular;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class ModuleContainerPanYOnly extends ModuleBase {

	protected int currentPosX, currentPosY;
	protected int screenSizeX;
	protected int screenSizeY;
	protected int containerSizeX;
	protected int containerSizeY;
	protected List<ModuleBase> moduleList;
	protected List<ModuleBase> staticModuleList;
	protected List<AbstractButton> buttonList, staticButtonList;
	protected List<Slot> slotList;
	protected double mouseLastX, mouseLastY;
	boolean outofBounds;
	ResourceLocation backdrop;
	protected int internalOffsetY;
	protected boolean mouseFirstDown = true;

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, 16, 16, 0, 0);
	}

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX, int paddingY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, paddingX, paddingY, 0, 0);
	}

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX , int paddingY, int containerSizeX, int containerSizeY) {
		super(offsetX, offsetY);
		this.moduleList = moduleList;
		this.staticModuleList = staticModules;
		outofBounds = true;

		this.screenSizeX = screenSizeX;
		this.screenSizeY = screenSizeY;

		buttonList = new LinkedList<>();
		staticButtonList = new LinkedList<>();
		slotList = new LinkedList<>();

		this.backdrop = backdrop;

		if(containerSizeX == 0 || containerSizeY == 0) {
			//AutoSize the container -----
			int maxX = 0, maxY = 0;
			for(ModuleBase module : moduleList) {
				if(module.offsetX > maxX)
					maxX = module.offsetX;
				if(module.offsetY > maxY)
					maxY = module.offsetY;
			}

			this.containerSizeX = maxX + paddingX;
			this.containerSizeY = maxY + paddingY;
			// -----------------------------
		}
		else {
			this.containerSizeX = containerSizeX;
			this.containerSizeY = containerSizeY;
		}

		if(moduleList != null)
			for(ModuleBase module : this.moduleList) {
				module.offsetX += offsetX;
				module.offsetY += offsetY;
			}
	}

	@Override
	@OnlyIn(value= Dist.CLIENT)
	public List<AbstractButton> addButtons(int x, int y) {

		buttonList.clear();
		staticButtonList.clear();

		for(ModuleBase module : this.moduleList) {
			buttonList.addAll(module.addButtons(0, 0));
		}

		for(ModuleBase module : this.staticModuleList) {
			staticButtonList.addAll(module.addButtons(x, y));
		}

		return new LinkedList<>();
	}

	public void setOffset(int y) {
		internalOffsetY = y + screenSizeY;
	}

	public void setOffset2(int y) {
		int deltaY = -y - currentPosY;
		currentPosY += deltaY;

		for(AbstractButton button2 : buttonList) {
			button2.y += deltaY;
		}

		for(ModuleBase module : moduleList) {
			module.offsetY += deltaY;
		}
	}

	public int getScrollY() {
		return currentPosY;
	}

	@Override
	public List<Slot> getSlots(Container container) {
		List<Slot> list = new LinkedList<>();

		for(ModuleBase module : this.moduleList) {
			list.addAll(module.getSlots(container));
		}

		for(ModuleBase module : this.staticModuleList) {
			list.addAll(module.getSlots(container));
		}

		slotList = list;
		return list;
	}

	@Override
	@OnlyIn(value= Dist.CLIENT)
	public void actionPerform(AbstractButton button) {

		for(ModuleBase module : moduleList)
			module.actionPerform(button);

		for(ModuleBase module : staticModuleList)
			module.actionPerform(button);
	}

	public void onScroll(int dwheel) {
		if(dwheel < 0) {
			moveContainerInterior(-20);
		}
		else if(dwheel > 0) {
			moveContainerInterior(20);
		}
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, ContainerScreen<? extends Container>  gui, FontRenderer font) {

		//Handle scrolling
		int scrollDelta = (int)LibVulpes.proxy.getScrollDelta(); //ObfuscationReflectionHelper.getPrivateValue( MouseHelper.class, Minecraft.getInstance().mouseHelper, "accumulatedScrollDelta");
		if(isMouseInBounds(0, 0, mouseX, mouseY) && scrollDelta != 0 )
			onScroll(scrollDelta);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		setUpScissor((ContainerScreen<Container>) gui, offsetX + guiOffsetX, guiOffsetY + offsetY, offsetX + screenSizeX, offsetY + screenSizeY);

		for(AbstractButton btn : buttonList) {
			btn.render(mat, mouseX, mouseY, zLevel);
		}

		for(AbstractButton btn : staticButtonList) {
			btn.render(mat, mouseX, mouseY, zLevel);
		}

		for(ModuleBase module : moduleList)
			module.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		for(ModuleBase module : staticModuleList)
			module.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);


		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@OnlyIn(value=Dist.CLIENT)
	protected void setUpScissor(ContainerScreen<Container> gui, int screenOffsetX, int screenOffsetY, int screenSizeX, int screenSizeY) {
		float multiplierX = gui.getMinecraft().getMainWindow().getWidth() / (float)gui.width;
		float multiplierY = gui.getMinecraft().getMainWindow().getHeight() / (float)gui.height;

		GL11.glScissor((int)( screenOffsetX*multiplierX), gui.getMinecraft().getMainWindow().getHeight() - (int)((screenOffsetY + screenSizeY)*multiplierY), (int)(screenSizeX*multiplierX), (int)(screenSizeY*multiplierY));

	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {
		super.onMouseClicked(gui, x, y, button);

		double scaledX = x;
		double scaledY = y;

		//Handles buttons (mostly vanilla copy)
		if(button == 0 && isMouseInBounds(0, 0, x, y)) {

			List<AbstractButton> fullButtonList = new LinkedList<>();
			fullButtonList.addAll(buttonList);
			fullButtonList.addAll(staticButtonList);


			for(AbstractButton iguieventlistener : fullButtonList) {
				if (iguieventlistener.isMouseOver(scaledX, scaledY)) {
					AbstractButton button2 = iguieventlistener;
					button2.playDownSound(gui.getMinecraft().getSoundHandler());
					gui.setListener(button2);
				}
			}
		}
		mouseFirstDown = true;
	}

	@OnlyIn(value= Dist.CLIENT)
	private boolean isMouseInBounds(int x , int y, double x2, double y2) {
		double transformedMouseX = x2 - x - offsetX;
		double transformedMouseY = y2 - y - offsetY;
		//return true;
		return transformedMouseX > 0 && transformedMouseX < screenSizeX + offsetX && transformedMouseY > 0 && transformedMouseY < screenSizeY + offsetY;
	}

	protected void moveContainerInterior(int deltaY) {
		//Clamp bounds ------------------------------------------------
		if(deltaY > 0) {
			deltaY = Math.min(deltaY, -currentPosY);
		}
		else if(deltaY < 0) {
			deltaY = Math.max(deltaY, -containerSizeY - currentPosY);
		}
		//--------------------------------------------------------------
		currentPosY += deltaY;

		//Transform
		try
		{
			// Need write access to the field!
			Field xPos = ObfuscationReflectionHelper.findField(Slot.class, "field_75223_e");
			Field yPos = ObfuscationReflectionHelper.findField(Slot.class, "field_75221_f");

			xPos.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(xPos, xPos.getModifiers() & ~Modifier.FINAL);

			yPos.setAccessible(true);
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(yPos, yPos.getModifiers() & ~Modifier.FINAL);


			for(Slot slot : slotList) {
				yPos.setInt(slot, slot.yPos + deltaY );
			}
		}
		catch( SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e)
		{
		}

		for(AbstractButton button2 : buttonList) {
			button2.y += deltaY;
		}


		for(ModuleBase module : moduleList) {
			module.offsetY += deltaY;
		}
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onMouseClickedAndDragged(double x, double y, int button) {

		if(isMouseInBounds(0, 0, x, y) ) {

			if(outofBounds || mouseFirstDown) {
				mouseLastX = x;
				mouseLastY = y;
				outofBounds = false;
				mouseFirstDown = false;
			}
			else if(mouseLastX != x && mouseLastY != y) {

				int deltaY = (int) ((y - mouseLastY));

				moveContainerInterior(deltaY);

				mouseLastX = x;
				mouseLastY = y;
			}
		}
		else {
			outofBounds = true;
		}
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
								 FontRenderer font) {

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		setUpScissor((ContainerScreen<Container>) gui, x + offsetX, y + offsetY, screenSizeX + offsetX, screenSizeY + offsetY);

		if(backdrop != null) {
			gui.getMinecraft().getTextureManager().bindTexture(backdrop);
			gui.blit(mat, x + offsetX, y + offsetY, (int)(-0.1*currentPosX), (int)(-0.1*currentPosY), screenSizeX + offsetX,  screenSizeY + offsetY);
		}

		for(AbstractButton button : buttonList)
			button.render(mat, mouseX, mouseY, 0);

		for(AbstractButton button : staticButtonList)
			button.render(mat, mouseX, mouseY, 0);

		for(ModuleBase module : moduleList) {
			module.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		}

		for(ModuleBase module : staticModuleList) {
			module.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

	}

	@Override
	public void setEnabled(boolean state) {
		if(state && !isEnabled()) {
			moveContainerInterior(0);
		}
		else if(!state && isEnabled()) {
			moveContainerInterior(0);
		}
		super.setEnabled(state);
	}
}
