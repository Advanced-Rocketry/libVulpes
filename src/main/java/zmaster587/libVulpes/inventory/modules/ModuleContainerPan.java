package zmaster587.libVulpes.inventory.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiModular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ModuleContainerPan extends ModuleBase {

	protected int currentPosX, currentPosY;
	protected int screenSizeX;
	protected int screenSizeY;
	protected int containerSizeX;
	protected int containerSizeY;
	protected List<ModuleBase> moduleList;
	protected List<ModuleBase> staticModuleList;
	protected List<Button> buttonList, staticButtonList;
	protected List<Slot> slotList;
	protected double mouseLastX, mouseLastY;
	boolean outofBounds;
	ResourceLocation backdrop;
	protected int internalOffsetX;
	protected int internalOffsetY;
	protected boolean mouseFirstDown = true;

	public ModuleContainerPan(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, 16, 16, 0, 0);
	}

	public ModuleContainerPan(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX, int paddingY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, paddingX, paddingY, 0, 0);
	}

	public ModuleContainerPan(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX ,int paddingY, int containerSizeX, int containerSizeY) {
		super(offsetX, offsetY);
		this.moduleList = moduleList;
		this.staticModuleList = staticModules;
		outofBounds = true;

		this.screenSizeX = screenSizeX;
		this.screenSizeY = screenSizeY;

		buttonList = new LinkedList<Button>();
		staticButtonList = new LinkedList<Button>();
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
	@OnlyIn(value=Dist.CLIENT)
	public List<Button> addButtons(int x, int y) {

		buttonList.clear();
		staticButtonList.clear();

		for(ModuleBase module : this.moduleList) {
			buttonList.addAll(module.addButtons(0, 0));
		}

		for(ModuleBase module : this.staticModuleList) {
			staticButtonList.addAll(module.addButtons(x, y));
		}

		return new LinkedList<Button>();
	}

	public void setOffset(int x, int y) {
		internalOffsetX = x + screenSizeX;
		internalOffsetY = y + screenSizeY;
	}

	public void setOffset2(int x ,int y) {
		int deltaX = -x - currentPosX;
		int deltaY = -y - currentPosY;
		currentPosX += deltaX;
		currentPosY += deltaY;

		//Transform

		try
		{
			// Need write access to the field!
			Field xPos = ObfuscationReflectionHelper.findField(Slot.class, "xPos");
			Field yPos = ObfuscationReflectionHelper.findField(Slot.class, "yPos");

			xPos.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(xPos, xPos.getModifiers() & ~Modifier.FINAL);

			yPos.setAccessible(true);
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(yPos, yPos.getModifiers() & ~Modifier.FINAL);


			for(Slot slot : slotList) {

				xPos.setInt(slot, slot.xPos + deltaX );
				yPos.setInt(slot, slot.yPos + deltaY );
			}
		}
		catch( SecurityException e)
		{
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}

		for(Button button2 : buttonList) {
			button2.x += deltaX;
			button2.y += deltaY;
		}

		for(ModuleBase module : moduleList) {
			module.offsetX += deltaX;
			module.offsetY += deltaY;
		}
	}

	public int getScrollX() {
		return currentPosX;
	}

	public int getScrollY() {
		return currentPosY;
	}

	@Override
	public List<Slot> getSlots(Container container) {
		List<Slot> list = new LinkedList<Slot>();

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
	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(Button button) {

		for(ModuleBase module : moduleList)
			module.actionPerform(button);

		for(ModuleBase module : staticModuleList)
			module.actionPerform(button);
	}

	public void onScroll(double dwheel) {
		if(dwheel < 0) {
			moveContainerInterior(0, -3);
		}
		else if(dwheel > 0) {
			moveContainerInterior(0, 3);
		}
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			ContainerScreen<? extends Container>  gui, FontRenderer font) {

		//Handle scrolling
		double scrollDelta = LibVulpes.proxy.getScrollDelta(); //ObfuscationReflectionHelper.getPrivateValue( MouseHelper.class, Minecraft.getInstance().mouseHelper, "accumulatedScrollDelta");
		if(isMouseInBounds(0, 0, mouseX, mouseY) && scrollDelta != 0 )
			onScroll(scrollDelta);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		setUpScissor((ContainerScreen<Container>) gui, offsetX + guiOffsetX, guiOffsetY + offsetY, offsetX + screenSizeX, offsetY + screenSizeY);

		for(Button btn : buttonList)
		{
			btn.render(mat, mouseX, mouseY, zLevel);
		}
		
		for(Button btn : staticButtonList)
		{
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

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {
		super.onMouseClicked(gui, x, y, button);

		double scaledX = x;
		double scaledY = y;

		//mouseLastX = scaledX;
		//mouseLastY = scaledY;

		//Handles buttons (mostly vanilla copy)
		if(button == 0 && isMouseInBounds(0, 0, x, y)) {

			List<Button> fullButtonList = new LinkedList<Button>();
			fullButtonList.addAll(buttonList);
			fullButtonList.addAll(staticButtonList);

			
			for(IGuiEventListener iguieventlistener : fullButtonList) {
				if (iguieventlistener.isMouseOver(scaledX, scaledY)) {
					Button button2 = (Button)iguieventlistener;
					button2.playDownSound(gui.getMinecraft().getSoundHandler());
					gui.setListener(button2);
				}
			}
		}
		mouseFirstDown = true;
	}

	@OnlyIn(value=Dist.CLIENT)
	private boolean isMouseInBounds(int x , int y, double x2, double y2) {
		double transformedMouseX = x2 - x - offsetX;
		double transformedMouseY = y2 - y - offsetY;
		//return true;
		return transformedMouseX > 0 && transformedMouseX < screenSizeX + offsetX && transformedMouseY > 0 && transformedMouseY < screenSizeY + offsetY;
	}

	protected void moveContainerInterior(int deltaX , int deltaY) {
		//Clamp bounds ------------------------------------------------
		if(deltaX > 0) {
			deltaX = Math.min(deltaX, -currentPosX);
		}
		else if(deltaX < 0) {
			deltaX = Math.max(deltaX, -containerSizeX - currentPosX);
		}
		if(deltaY > 0) {
			deltaY = Math.min(deltaY, -currentPosY);
		}
		else if(deltaY < 0) {
			deltaY = Math.max(deltaY, -containerSizeY - currentPosY);
		}
		//--------------------------------------------------------------

		currentPosX += deltaX;
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

				xPos.setInt(slot, slot.xPos + deltaX );
				yPos.setInt(slot, slot.yPos + deltaY );
			}
		}
		catch( SecurityException e)
		{
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}

		for(Button button2 : buttonList) {
			button2.x += deltaX;
			button2.y += deltaY;
		}


		for(ModuleBase module : moduleList) {
			module.offsetX += deltaX;
			module.offsetY += deltaY;
		}
	}

	//DO the actual scrolling
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

				int deltaX = (int) ((x - mouseLastX));
				int deltaY = (int) ((y - mouseLastY));

				moveContainerInterior(deltaX, deltaY);

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

		for(Button button : buttonList)
			button.renderButton(mat, mouseX, mouseY, 0);

		for(Button button : staticButtonList)
			button.renderButton(mat, mouseX, mouseY, 0);

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
			moveContainerInterior(10000, 0);
		}
		else if(!state && isEnabled()) {
			moveContainerInterior(-10000, 0);
		}
		super.setEnabled(state);
	}
}
