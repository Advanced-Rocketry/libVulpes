package zmaster587.libVulpes.inventory;

import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class TextureResources {
	
	public static final  ResourceLocation starryBG 	   = new ResourceLocation("libvulpes", "textures/gui/starrybg.png");
	public static final  ResourceLocation buttonNull[] = {new ResourceLocation("libvulpes","textures/gui/null.png" )};
	public static final  ResourceLocation buttonScan[] = {new ResourceLocation("libvulpes", "textures/gui/guiscan.png"), new ResourceLocation("libvulpes", "textures/gui/guiscan_hover.png"), new ResourceLocation("libvulpes", "textures/gui/guiscan_pressed.png"), null};
	public static final  ResourceLocation buttonBuild[] = {new ResourceLocation("libvulpes", "textures/gui/guibuttonred.png"), new ResourceLocation("libvulpes", "textures/gui/guibuttonred_hover.png"), new ResourceLocation("libvulpes", "textures/gui/guibuttonred_pressed.png"),  new ResourceLocation("libvulpes", "textures/gui/guibuttonred_disabled.png")};
	public static final  ResourceLocation buttonDown[] = {new ResourceLocation("libvulpes", "textures/gui/guiarrowdown.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowdown_hover.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowdown_pressed.png"), null};
	public static final  ResourceLocation buttonLeft[] = {new ResourceLocation("libvulpes", "textures/gui/guiarrowleft.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowleft_hover.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowleft_pressed.png"), null};
	public static final  ResourceLocation buttonRight[] = {new ResourceLocation("libvulpes", "textures/gui/guiarrowright.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowright_hover.png"), new ResourceLocation("libvulpes", "textures/gui/guiarrowright_pressed.png"), null};
	public static final  ResourceLocation buttonSquare[] = {new ResourceLocation("libvulpes", "textures/gui/buttonsquare.png"), new ResourceLocation("libvulpes", "textures/gui/buttonsquare_hover.png"), new ResourceLocation("libvulpes", "textures/gui/buttonsquare.png"), null};
	public static final  ResourceLocation buttonToggleImage[] = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/switchoff.png"), new ResourceLocation("libvulpes:textures/gui/buttons/switchon.png")};
	

	public static final  ResourceLocation buttonRedstoneActive[] = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstoneallowed.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstoneallowed_hover.png")};
	public static final  ResourceLocation buttonRedstoneInverted[] = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstoneinverted.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstoneinverted_hover.png")};
	public static final  ResourceLocation buttonRedstoneDisabled[] = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstonenotallowed.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonredstonenotallowed_hover.png")};
	
	public static final ResourceLocation coalGeneratorBarImg = new ResourceLocation("libvulpes", "textures/gui/coalgenerator.png");
	
	public static final ProgressBarImage progressGenerator = new ProgressBarImage(16, 0, 16, 16, 0, 0, 16, 16, Direction.UP, coalGeneratorBarImg);
	public static final IconResource	slotDisabled = new IconResource(230, 36, 18, 18, new ResourceLocation("libvulpes","textures/gui/maingui.png"));
}
