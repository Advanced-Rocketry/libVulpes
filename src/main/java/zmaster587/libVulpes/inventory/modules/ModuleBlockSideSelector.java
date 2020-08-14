package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.util.BlockDirectionFunction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class ModuleBlockSideSelector extends ModuleBase implements IButtonInventory {

	private IGuiCallback callback;
	private ModuleButton buttons[];
	private BlockDirectionFunction bdf;
	private String text[];

	private static final String directions[] = {"Bottom: ", "Top: ", "North: ", "South ", "West: ", "East: "};
	private static final int colors[] = {0x00DD00, 0xDD0000, 0x0000DD, 0xDDDD00, 0xDD00DD, 0x00DDDD};

	public ModuleBlockSideSelector(int offsetX, int offsetY, IGuiCallback callback, String ... stateNames) {
		super(offsetX, offsetY);
		this.callback = callback;
		bdf = new BlockDirectionFunction(stateNames.length);
		text = stateNames;

		buttons = new ModuleButton[] {new ModuleButton(offsetX + 42, offsetY + 42, "", this, TextureResources.buttonSquare, directions[0] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY + 21, "", this, TextureResources.buttonSquare, directions[1] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY, "", this, TextureResources.buttonSquare, directions[2] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY + 42, "", this, TextureResources.buttonSquare, directions[3] + stateNames[0], 16, 16),
				new ModuleButton(offsetX, offsetY + 21, "", this, TextureResources.buttonSquare, directions[4] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 42, offsetY + 21, "", this, TextureResources.buttonSquare, directions[5] + stateNames[0], 16, 16)};

		if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT)
			for(ModuleButton button : buttons)
				button.setBGColor(colors[0]);
	}

	@Override
	public void actionPerform(Button button) {
		for(ModuleButton button2 : buttons)
			button2.actionPerform(button);
	}

	@Override
	public List<Button> addButtons(int x, int y) {

		List<Button> list = super.addButtons(x, y);
		for(int i = 0; i < 6; i++)
			list.addAll(buttons[i].addButtons(x, y));
		return list;
	}

	@Override
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack matrix, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);

		for(ModuleBase button : buttons)
			button.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);
	}

	@Override
	public void renderForeground(MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, ContainerScreen<? extends Container>  gui, FontRenderer font) {
		// TODO Auto-generated method stub
		super.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);
		for(ModuleBase button : buttons)
			button.renderForeground(mat, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
					font);
	}

	public int getStateForSide(Direction side) {
		return getStateForSide(side.ordinal());
	}

	public int getStateForSide(int side) {
		return bdf.getState(side);
	}

	public void setStateForSide(Direction side, int state) {
		setStateForSide(side.ordinal(), state);
	}

	public void setStateForSide(int sideNum, int state) {
		bdf.setState(sideNum, state);
		if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
			buttons[sideNum].setBGColor(colors[bdf.getState(sideNum) % colors.length]);
			buttons[sideNum].setToolTipText(directions[sideNum] + text[bdf.getState(sideNum)]);
		}

	}

	public void write(CompoundNBT nbt) {
		bdf.write(nbt);
	}

	public void readFromNBT(CompoundNBT nbt) {
		bdf.readFromNBT(nbt);
		if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) 
			for(int i = 0 ; i < buttons.length; i++) {
				buttons[i].setBGColor(colors[bdf.getState(i) % colors.length]);
				buttons[i].setToolTipText(directions[i] + text[bdf.getState(i)]);
			}

	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		
		int index = 0;
		for(ModuleButton button : buttons)
		{
			if(button == buttonId)
				break;
			index++;
		}
		
		bdf.advanceState(index);
		buttons[index].setBGColor(colors[bdf.getState(index) % colors.length]);
		buttons[index].setToolTipText(directions[index] + text[bdf.getState(index)]);

		callback.onModuleUpdated(this);
	}

}
