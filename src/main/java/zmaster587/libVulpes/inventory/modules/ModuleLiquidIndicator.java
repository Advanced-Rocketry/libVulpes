package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.libVulpes.util.IFluidHandlerInternal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;

public class ModuleLiquidIndicator extends ModuleBase {

	IFluidHandler tile;
	IFluidHandlerInternal tile2;

	ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");

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

	private Fluid getFluid(int id)
	{

		for(Fluid fluid : ForgeRegistries.FLUIDS.getValues())
		{
			if(ForgeRegistries.FLUIDS.getKey(fluid).hashCode() == id)
				return fluid;
		}
		return Fluids.WATER;
	}

	private int getFluidID(Fluid fluid)
	{
		return ForgeRegistries.FLUIDS.getKey(fluid).hashCode();
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		FluidStack info = tile.getFluidInTank(0);

		if(localId == 0 && info != null)
			crafter.sendWindowProperty(container, variableId, info.getAmount() & 0xFFFF);
		else if(localId == 1 && info != null)
			crafter.sendWindowProperty(container, variableId, (info.getAmount() >>> 16) & 0xFFFF);
		else if(localId == 2)
			if(info == null) 
				crafter.sendWindowProperty(container, variableId, invalidFluid);
			else
				crafter.sendWindowProperty(container, variableId, getFluidID(info.getFluid()));
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		FluidStack info = tile.getFluidInTank(0);
		int tankCapacity = tile.getTankCapacity(0);

		if(slot == 2) {
			if((info == null || info.getFluid().isEquivalentTo(Fluids.EMPTY)) && value != invalidFluid) {
				if(tile2 != null)
					tile2.fillInternal(new FluidStack(getFluid(value), 1), FluidAction.EXECUTE);
				else
					tile.fill(new FluidStack(getFluid(value), 1), FluidAction.EXECUTE);
			}
			else if(value == invalidFluid) {
				if(tile2 != null) 
					tile2.drainInternal(tankCapacity, FluidAction.EXECUTE);
				else
					tile.drain(tankCapacity, FluidAction.EXECUTE);
			}
			else if(info != null && value != getFluidID(info.getFluid())) { //Empty the tank then fill it back up with new resource
				FluidStack stack;
				if(tile2 != null)
					stack = tile2.drainInternal(tankCapacity, FluidAction.EXECUTE);
				else
					stack = tile.drain(tankCapacity, FluidAction.EXECUTE);

				stack = new FluidStack(stack.getFluid(), stack.getAmount());

				tile.fill(stack, FluidAction.EXECUTE);
			}
		}
		else if((slot == 0 || slot == 1) && info != null) {
			int difference;

			if(slot == 0) {
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
			return (info != null && prevLiquidAmt != info.getAmount());
		}
		else if(localId == 2) {
			if(info == null)
				return prevLiquidUUID != invalidFluid;
			else
				return getFluidID(info.getFluid()) != prevLiquidUUID;
		}

		return false;
	}

	@Override
	protected void updatePreviousState(int localId) {
		FluidStack info = tile.getFluidInTank(0);
		if(localId == 0 && info != null)
			prevLiquidAmt =  info.getAmount();
		else if(localId == 1)
			if( info == null) 
				prevLiquidUUID = invalidFluid;
			else
				prevLiquidUUID = getFluidID(info.getFluid());
	}

	protected float getProgress() {

		int capacity = 0;
		int fillAmount = 0;

		for(int i = 0; i < tile.getTanks(); i++) {
			FluidStack fluidInfo = tile.getFluidInTank(i);
			capacity += tile.getTankCapacity(i);
			if(fluidInfo != null)
				fillAmount += fluidInfo.getAmount();
		}

		return fillAmount/(float)capacity;
	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void renderForeground (MatrixStack mat, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, ContainerScreen<? extends Container>  gui, FontRenderer font) {

		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;
		int ySize = 52;
		int xSize = 12;

		if( relativeX > 0 && relativeX < xSize && relativeY > 0 && relativeY < ySize) {
			List<String> list = new LinkedList<String>();
			FluidStack fluidStack = tile.getFluidInTank(0);

			if(fluidStack!= null) {

				list.add(fluidStack.getDisplayName().getString() +": "+fluidStack.getAmount() + " / " + tile.getTankCapacity(0) + " mB");


			}
			else
				list.add("Empty");

			this.drawTooltip((ContainerScreen<Container>) gui, mat, list, mouseX, mouseY, zLevel, font);
		}

	}

	@Override
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack mat, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, mat, x, y, mouseX, mouseY,  font);
		gui.blit(mat, x + offsetX, y + offsetY, 176, 58, 14, 54);

		//Draw Fluid
		FluidStack info = tile.getFluidInTank(0);

		if(info == null)
			return;
		
		if(info.getFluid() == Fluids.EMPTY)
			return;


		TextureAtlasSprite sprite = info.getFluid() != Fluids.EMPTY ? ModelLoader.defaultTextureGetter().apply(ForgeHooksClient.getBlockMaterial(info.getFluid().getAttributes().getStillTexture())) : null;
		
		sprite.getAtlasTexture().bindTexture();
		int color = info.getFluid().getAttributes().getColor(info);

		GL11.glColor3b((byte)((color >>> 16) & 127), (byte)((color >>> 8) & 127), (byte)(color & 127));
		//GL11.glColor3b((byte)127, (byte)127, (byte)127);

		float percent = getProgress();
		int ySize = 52;
		int xSize = 12;

		if(sprite == null)
			gui.blit(mat, offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), 0, 0, xSize, (int)(percent*ySize));
		else {
			gui.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			gui.blit(mat, offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), 0 /* zlevel */, xSize, (int)(percent*ySize), sprite);
		}
		//gui.drawTexturedModelRectFrom(offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), fluidIcon, xSize, (int)(percent*ySize));

		//this.drawProgressBarIconVertical(x + 27, y + 18,, 12, 52, getProgress());
	}

}
