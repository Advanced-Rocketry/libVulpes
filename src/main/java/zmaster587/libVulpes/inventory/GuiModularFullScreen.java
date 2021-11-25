package zmaster587.libVulpes.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiModularFullScreen extends GuiModular {

	public GuiModularFullScreen(ContainerModular container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		
		this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
		this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
		this.width = Minecraft.getInstance().getMainWindow().getWidth();
		this.height = Minecraft.getInstance().getMainWindow().getHeight();
	}
	
	   public void init(Minecraft minecraft, int width, int height) {
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
