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
		this.field_230708_k_ = Minecraft.getInstance().getMainWindow().getWidth();
		this.field_230709_l_ = Minecraft.getInstance().getMainWindow().getHeight();
	}
	
	   public void func_231158_b_(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
		      this.field_230706_i_ = p_231158_1_;
		      this.field_230707_j_ = p_231158_1_.getItemRenderer();
		      this.field_230712_o_ = p_231158_1_.fontRenderer;
		      //this.field_230708_k_ = p_231158_2_;
		      //this.field_230709_l_ = p_231158_3_;
		      java.util.function.Consumer<Widget> remove = (b) -> {
		         field_230710_m_.remove(b);
		         field_230705_e_.remove(b);
		      };
		      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.field_230710_m_, this::func_230480_a_, remove))) {
		      this.field_230710_m_.clear();
		      this.field_230705_e_.clear();
		      this.func_231035_a_((IGuiEventListener)null);
		      this.func_231160_c_();
		      }
		      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.field_230710_m_, this::func_230480_a_, remove));
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
