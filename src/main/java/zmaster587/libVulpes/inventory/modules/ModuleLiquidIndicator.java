package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;

public class ModuleLiquidIndicator extends ModuleBase {

	IFluidHandler tile;
	IFluidHandlerInternal tile2;

	int prevLiquidUUID;
	int prevLiquidAmt;
	private static final int invalidFluid = -1;

	public ModuleLiquidIndicator(int offsetX, int offsetY, IFluidHandler progress) {
		super(offsetX, offsetY);
		this.tile = progress;
		if(progress instanceof IFluidHandlerInternal)
			this.tile2 = (IFluidHandlerInternal)progress;
	}

	//TODO: sync changes
	@Override
	public int numberOfChangesToSend() {
		return 3;
	}

	private Fluid getFluid(int id) {
		for(Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
			if((ForgeRegistries.FLUIDS.getKey(fluid).hashCode() & 0xFFFF) == id)
				return fluid;
		}
		return Fluids.EMPTY;
	}

	private int getFluidID(Fluid fluid) {
		return fluid.isEquivalentTo(Fluids.EMPTY) ? -1 : ForgeRegistries.FLUIDS.getKey(fluid).hashCode() & 0xFFFF;
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {
		FluidStack info = tile.getFluidInTank(0);

		if(localId == 0)
			if(info.isEmpty()) 
				crafter.sendWindowProperty(container, variableId, invalidFluid);
			else
				crafter.sendWindowProperty(container, variableId, getFluidID(info.getFluid()));
		if(localId == 1 && !info.isEmpty())
			crafter.sendWindowProperty(container, variableId, info.getAmount() & 0xFFFF);
		else if(localId == 2 && !info.isEmpty())
			crafter.sendWindowProperty(container, variableId, (info.getAmount() >>> 16) & 0xFFFF);
		
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		FluidStack info = tile.getFluidInTank(0);
		int tankCapacity = tile.getTankCapacity(0);

		if(slot == 0) {
			if((info.isEmpty() || info.getFluid().isEquivalentTo(Fluids.EMPTY)) && value != invalidFluid) {
				if(tile2 != null)
					tile2.fillInternal(new FluidStack(getFluid(value), 1), FluidAction.EXECUTE);
				else
					tile.fill(new FluidStack(getFluid(value), 1), FluidAction.EXECUTE);
			} else if(value == invalidFluid) {
				if(tile2 != null) 
					tile2.drainInternal(tankCapacity, FluidAction.EXECUTE);
				else
					tile.drain(tankCapacity, FluidAction.EXECUTE);
			} else if(!info.isEmpty() && value != getFluidID(info.getFluid())) { //Empty the tank then fill it back up with new resource
				FluidStack stack;
				if(tile2 != null)
					stack = tile2.drainInternal(tankCapacity, FluidAction.EXECUTE);
				else
					stack = tile.drain(tankCapacity, FluidAction.EXECUTE);

				stack = new FluidStack(stack.getFluid(), stack.getAmount());

				tile.fill(stack, FluidAction.EXECUTE);
			}
		} else if((slot == 1 || slot == 2) && !info.isEmpty()) {
			int difference;

			if(slot == 1) {
				difference = (value & 0xFFFF) - (info.getAmount() & 0xFFFF);
			}
			else
				difference = ((value << 16) & 0xFFFF0000) - (info.getAmount() & 0xFFFF0000);

			if(difference > 0) {
				if(tile2 != null)
					tile2.fillInternal(new FluidStack(info.getFluid(), difference), FluidAction.EXECUTE);
				else
					tile.fill(new FluidStack(info.getFluid(), difference), FluidAction.EXECUTE);
			}
			else
				if(tile2 != null)
					tile2.drainInternal(-difference, FluidAction.EXECUTE);
				else
					tile.drain(-difference, FluidAction.EXECUTE);
		}
	}

	@Override
	public boolean needsUpdate(int localId) {
		FluidStack info = tile.getFluidInTank(0);

		if(localId == 0 || localId == 1) {
			return (!info.isEmpty() && prevLiquidAmt != info.getAmount());
		} else if(localId == 2) {
			if(info.isEmpty())
				return prevLiquidUUID != invalidFluid;
			else
				return getFluidID(info.getFluid()) != prevLiquidUUID;
		}

		return false;
	}

	@Override
	protected void updatePreviousState(int localId) {
		FluidStack info = tile.getFluidInTank(0);
		if(localId == 0 && !info.isEmpty())
			prevLiquidAmt =  info.getAmount();
		else if(localId == 1)
			if(info.isEmpty())
				prevLiquidUUID = invalidFluid;
			else
				prevLiquidUUID = getFluidID(info.getFluid());
	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void renderForeground (MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, ContainerScreen<? extends Container>  gui, FontRenderer font) {
		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;
		int ySize = 52;
		int xSize = 12;

		if( relativeX > 0 && relativeX < xSize && relativeY > 0 && relativeY < ySize) {
			List<String> list = new LinkedList<>();
			FluidStack fluidStack = tile.getFluidInTank(0);

			if(!fluidStack.isEmpty()) {
				list.add(fluidStack.getDisplayName().getString() +": "+fluidStack.getAmount() + " / " + tile.getTankCapacity(0) + " mB");
			} else
				list.add("Empty");

			this.drawTooltip(gui, mat, list, mouseX, mouseY, zLevel, font);
		}

	}

	@Override
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack transform, int x, int y, int mouseX, int mouseY, FontRenderer font) {
		super.renderBackground(gui, transform, x, y, mouseX, mouseY,  font);
		gui.blit(transform, x + offsetX, y + offsetY, 176, 58, 14, 54);

		//Draw Fluid
		FluidStack fluid = tile.getFluidInTank(0);
		if(fluid.isEmpty()) return;

		x+=offsetX+1;
		y+=offsetY+1;

		if(!fluid.isEmpty()) {
			//Pre-render stuff
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			gui.getMinecraft().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
			transform.push();

			//Fluid-based stuff
			int fluidHeight = (int)(52*(fluid.getAmount()/(float)tile.getTankCapacity(0)));
			int color = fluid.getFluid().getAttributes().getColor(fluid);

            //Texture-based stuff
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(fluid.getFluid().getAttributes().getStillTexture());
			int iW = sprite.getWidth();
			int iH = sprite.getHeight();

			//Actual render
			RenderSystem.color4f((color >> 16 & 255)/255.0f, (color >> 8 & 255)/255.0f, (color & 255)/255.0f, 1);
			for (int i = 0; i < (1 + (fluidHeight-1)/iH); i++) {
				float maxU = sprite.getMinU() + (sprite.getMaxU()-sprite.getMinU())*12f/iW;
				float maxV = sprite.getMinV() + (sprite.getMaxV()-sprite.getMinV())*((fluidHeight-i*iH) >= iH ? iH : fluidHeight % iH)/(float)iH;
				innerBlit(transform.getLast().getMatrix(), x, x+12, y + 52-Math.min(fluidHeight, (i+1)*iH), y + 52-i*iH, 0, sprite.getMinU(), maxU, sprite.getMinV(), maxV);
			}

			//Post-render stuff
			transform.pop();
			RenderSystem.disableBlend();
			RenderSystem.color3f(1, 1, 1);
		}
	}

	private void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
		bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
		bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
		bufferbuilder.finishDrawing();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.draw(bufferbuilder);
	}
}
