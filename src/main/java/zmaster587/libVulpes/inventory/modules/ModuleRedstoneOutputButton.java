package zmaster587.libVulpes.inventory.modules;

import net.minecraft.client.gui.widget.button.AbstractButton;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleRedstoneOutputButton extends ModuleButton {

	RedstoneState state;
	
	String suppText;

	public ModuleRedstoneOutputButton(int offsetX, int offsetY,
			String text, IButtonInventory tile) {
		super(offsetX, offsetY, text, tile, TextureResources.buttonRedstoneActive, 24 ,24);
		sizeX=24;
		sizeY=24;
		state = RedstoneState.ON;
		suppText = "";
	}
	
	public ModuleRedstoneOutputButton(int offsetX, int offsetY, 
			String text, IButtonInventory tile, String text2) {
		this(offsetX, offsetY,
			text, tile);
		suppText = text2;
	}

	public RedstoneState getState() {
		return state;
	}

	public void setRedstoneState(int i) {
		state = RedstoneState.values()[i];
	}

	public void setRedstoneState(RedstoneState i) {
		state = i;
	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		if(state != null)

			switch(state) {
			case ON:
				button.setButtonTexture(TextureResources.buttonRedstoneActive);
				tooltipText = suppText + "Redstone control normal";
				break;
			case OFF:
				button.setButtonTexture(TextureResources.buttonRedstoneDisabled);
				tooltipText = suppText + "Redstone control disabled";
				break;
			case INVERTED:
				button.setButtonTexture(TextureResources.buttonRedstoneInverted);
				tooltipText = suppText + "Redstone control inverted";
			}

		super.renderBackground(gui, mat, x, y, mouseX, mouseY, font);
	}

	@OnlyIn(value=Dist.CLIENT)
	public void actionPerform(AbstractButton button) {
		if(enabled && button == this.button) {
			if(state == null)
				state = RedstoneState.ON;
			state = state.getNext();
			tile.onInventoryButtonPressed(this);
		}
	}
}
