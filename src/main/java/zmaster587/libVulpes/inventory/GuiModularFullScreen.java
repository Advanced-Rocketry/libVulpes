package zmaster587.libVulpes.inventory;

import java.util.List;

import javax.annotation.Nullable;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.ITextComponent;

public class GuiModularFullScreen extends GuiModular {

	public GuiModularFullScreen(ContainerModular container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		
		this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
		this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
		this.width = Minecraft.getInstance().getMainWindow().getWidth();
		this.height = Minecraft.getInstance().getMainWindow().getHeight();
	}
	
	   public void func_231158_b_(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
		      this.minecraft = p_231158_1_;
		      this.itemRenderer = p_231158_1_.getItemRenderer();
		      this.font = p_231158_1_.fontRenderer;
		      //this.width = p_231158_2_;
		      //this.height = p_231158_3_;
		      java.util.function.Consumer<Widget> remove = (b) -> {
		         buttons.remove(b);
		         children.remove(b);
		      };
		      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
		      this.buttons.clear();
		      this.children.clear();
		      this.setListener((IGuiEventListener)null);
		      this.init();
		      }
		      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
		   }
	
	// initgui
	@Override
	public void init() {
		this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
		this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
		this.width = Minecraft.getInstance().getMainWindow().getWidth();
		this.height = Minecraft.getInstance().getMainWindow().getHeight();
		
		super.init();
	}
}
