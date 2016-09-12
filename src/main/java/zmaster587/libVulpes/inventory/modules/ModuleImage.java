package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.util.IconResource;

@SideOnly(Side.CLIENT)
public class ModuleImage extends ModuleBase {

	IconResource icon;
	
	public ModuleImage(int offsetX, int offsetY, IconResource icon) {
		super(offsetX, offsetY);
		this.icon = icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);
		
		GL11.glEnable(GL11.GL_BLEND);
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getResourceLocation());
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, icon.getxLoc(), icon.getyLoc(), icon.getxSize(), icon.getySize());
		GL11.glDisable(GL11.GL_BLEND);
	}
	
}
