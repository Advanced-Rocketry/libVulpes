package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.gui.GuiToggleButtonImage;

public class ModuleItemToggleButton extends ModuleToggleSwitch {

	ItemStack item;
	
	public ModuleItemToggleButton(int offsetX, int offsetY, String text, IToggleButton tile,
			ResourceLocation[] buttonImages, boolean defaultState, ItemStack item) {
		super(offsetX, offsetY, text, tile, buttonImages, defaultState);
		
		this.item = item;
	}
	
	@OnlyIn(value=Dist.CLIENT)
	public List<Button> addButtons(int x, int y) {

		List<Button> list = new LinkedList<Button>();

		enabledButton = new GuiToggleButtonImage(x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);
		enabledButton.setState(currentState);

		list.add(enabledButton);

		return list;
	}
}
