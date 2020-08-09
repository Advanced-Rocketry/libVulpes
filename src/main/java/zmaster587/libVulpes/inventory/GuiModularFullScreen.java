package zmaster587.libVulpes.inventory;

import java.util.List;

import javax.annotation.Nullable;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.ITextComponent;

public class GuiModularFullScreen extends GuiModular {

	public GuiModularFullScreen(ContainerModular container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		
		this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
		this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
		this.field_230708_k_ = Minecraft.getInstance().getMainWindow().getWidth();
		this.field_230709_l_ = Minecraft.getInstance().getMainWindow().getHeight();
	}
	
	// initgui
	@Override
	public void func_231160_c_() {
		this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
		this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
		this.field_230708_k_ = Minecraft.getInstance().getMainWindow().getWidth();
		this.field_230709_l_ = Minecraft.getInstance().getMainWindow().getHeight();
		
		super.func_231160_c_();
	}
}
