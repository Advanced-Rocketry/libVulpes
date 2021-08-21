package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.TextureResources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ModuleSlotButton extends ModuleButton {

	ItemStack stack;
	World worldObj;

	public ModuleSlotButton(int offsetX, int offsetY, IButtonInventory tile, ItemStack slotDisplay,  World world ) {

		super(offsetX, offsetY , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName().getString() ,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	public ModuleSlotButton(int offsetX, int offsetY, IButtonInventory tile, ItemStack slotDisplay, String extraDisplay,  World world ) {

		super(offsetX, offsetY , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName().getString() + " \n" + extraDisplay,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();


		textureManager.bindTexture(CommonResources.genericBackground);
		gui.blit(mat, x + offsetX - 1, y + offsetY - 1, 176, 0, 18, 18);

		int p_77015_4_ = x + offsetX;
		int p_77015_5_ = y + offsetY;

		int zLevel = 500;

		int k = stack.getDamage();
		int l;
		float f;
		float f3;
		float f4;

		RenderSystem.depthMask(false);
		IRenderTypeBuffer.Impl buffer =Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, p_77015_4_, p_77015_5_);

	}

}
