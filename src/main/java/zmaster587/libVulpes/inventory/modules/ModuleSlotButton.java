package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleSlotButton extends ModuleButton {

	ItemStack stack;
	World worldObj;

	public ModuleSlotButton(int offsetX, int offsetY, IButtonInventory tile, ItemStack slotDisplay,  World world ) {

		super(offsetX, offsetY , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName().getString() ,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	public ModuleSlotButton(int offsetX, int offsetY, IButtonInventory tile, ItemStack slotDisplay, String extraDisplay,  World world ) {

		super(offsetX, offsetY , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName() + " \n" + extraDisplay,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container>  gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();


		textureManager.bindTexture(CommonResources.genericBackground);
		gui.func_238474_b_(mat, x + offsetX - 1, y + offsetY - 1, 176, 0, 18, 18);

		int p_77015_4_ = x + offsetX;
		int p_77015_5_ = y + offsetY;

		int zLevel = 500;


		int k = stack.getDamage();
		int l;
		float f;
		float f3;
		float f4;


		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);



		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
		GL11.glDisable(GL11.GL_BLEND);


		GL11.glPushMatrix();
		GL11.glTranslatef((float)(p_77015_4_ + 8), (float)(p_77015_5_ + 12), -3.0F + zLevel);
		GL11.glScalef(10.0F, 10.0F, 10.0F);

		GL11.glScalef(1.0F, 1.0F, -1.0F);
		GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotated(45.0F + ((System.currentTimeMillis() % 200000)/50F) * 2, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-.5f,-255,-.5f);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		IRenderTypeBuffer.Impl buffer =Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		Block itemBlock = Block.getBlockFromItem(stack.getItem());
		if(itemBlock != null) {
			BlockState block = itemBlock.getDefaultState();
			Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(block, mat, Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), 255, 255);
			Tessellator.getInstance().draw();
		}

		GL11.glPopMatrix();

	}

}
