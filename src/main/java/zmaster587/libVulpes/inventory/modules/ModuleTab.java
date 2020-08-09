package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.inventory.GuiModular;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleTab extends ModuleBase implements IButtonInventory {

	private int tab;
	IGuiCallback gameObject;
	private ModuleButton buttons[];
	
	public ModuleTab(int offsetX, int offsetY,int startingId, IGuiCallback object, int numTabs, String[] tabText, ResourceLocation[][] textures) {
		super(offsetX, offsetY);
		gameObject = object;
		
		buttons = new ModuleButton[numTabs];
		
		for(int i = 0; i  < numTabs; i++) {
			buttons[i] = new ModuleButton(offsetX + i*24, offsetY - 20, i, "", this, textures[i], tabText[i], 24,24);
		}
		sizeX = 24;
		sizeY = 24;
	}
	
	
	public void setTab(int tabNum) { tab = tabNum; }
	
	public int getTab() { return tab; }
	
	@Override
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {
		super.onMouseClicked(gui, x, y, button);
		
		for(ModuleButton button2 : buttons)
			button2.onMouseClicked(gui, x, y, button);
	}
	
	@Override
	public List<Button> addButtons(int x, int y) {
		List<Button> list = super.addButtons(x, y);
		
		for(ModuleButton button2 : buttons) list.addAll(button2.addButtons(x, y));
		
		return list;
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(Button button) {
		super.actionPerform(button);
		
		for(ModuleButton button2 : buttons)
			button2.actionPerform(button);
	}
	
	@Override
	public void renderForeground(MatrixStack buf, int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, ContainerScreen<? extends Container>  gui, FontRenderer font) {
		// TODO Auto-generated method stub
		super.renderForeground(buf, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);
		
		for(ModuleButton button2 : buttons)
			button2.renderForeground(buf, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
					font);
		
	}
	
	@Override
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack buf, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, buf, x, y, mouseX, mouseY, font);
		
		for(ModuleButton button2 : buttons)
			button2.renderBackground(gui, buf, x, y, mouseX, mouseY, font);
	}


	@Override
	public void onInventoryButtonPressed(int buttonId) {
		tab = buttonId;
		gameObject.onModuleUpdated(this);
	}

}
