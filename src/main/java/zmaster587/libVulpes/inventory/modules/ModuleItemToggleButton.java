package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
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
	public List<AbstractButton> addButtons(int x, int y) {

		List<AbstractButton> list = new LinkedList<>();

		button = new GuiToggleButtonImage(x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);
		((GuiToggleButtonImage)button).setState(currentState);

		list.add(button);

		return list;
	}
}
