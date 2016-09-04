package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class ModuleLiquidIndicator extends ModuleBase {

	IFluidHandler tile;

	int prevLiquidUUID;
	int prevLiquidAmt;
	private static final int invalidFluid = -1;

	public ModuleLiquidIndicator(int offsetX, int offsetY, IFluidHandler progress) {
		super(offsetX, offsetY);
		this.tile = progress;
	}

	//TODO: sync changes
	@Override
	public int numberOfChangesToSend() {
		return 3;
	}

	@Override
	public void sendChanges(Container container, ICrafting crafter,
			int variableId, int localId) {
		FluidTankInfo info = tile.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if(localId == 0 && info.fluid != null)
			crafter.sendProgressBarUpdate(container, variableId, info.fluid.amount & 0xFFFF);
		else if(localId == 1 && info.fluid != null)
			crafter.sendProgressBarUpdate(container, variableId, (info.fluid.amount >>> 16) & 0xFFFF);
		else if(localId == 2)
			if(info.fluid == null) 
				crafter.sendProgressBarUpdate(container, variableId, invalidFluid);
			else
				crafter.sendProgressBarUpdate(container, variableId, info.fluid.getFluidID());
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		FluidTankInfo info[] = tile.getTankInfo(ForgeDirection.UNKNOWN);

		if(slot == 2) {
			if(info[0].fluid == null && value != invalidFluid) {
				tile.fill(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.getFluid(value), 1), true);
			}
			else if(value == invalidFluid) {
				tile.drain(ForgeDirection.UNKNOWN, info[0].capacity, true);
			}
			else if(info[0].fluid != null && value != info[0].fluid.getFluidID()) { //Empty the tank then fill it back up with new resource
				FluidStack stack = tile.drain(ForgeDirection.UNKNOWN, info[0].capacity, true);
				stack = new FluidStack(FluidRegistry.getFluid(stack.getFluidID()), stack.amount);

				tile.fill(ForgeDirection.UNKNOWN, stack, true);
			}
		}
		else if((slot == 0 || slot == 1) && info[0].fluid != null) {
			int difference;
			
			if(slot == 0) {
				difference = (value & 0xFFFF) - (info[0].fluid.amount & 0xFFFF);
			}
			else
				difference = ((value << 16) & 0xFFFF0000) - (info[0].fluid.amount & 0xFFFF0000);

			if(difference > 0) {
				tile.fill(ForgeDirection.UNKNOWN, new FluidStack(info[0].fluid.getFluid(), difference), true);
			}
			else
				tile.drain(ForgeDirection.UNKNOWN, -difference, true);
		}
	}

	@Override
	public boolean needsUpdate(int localId) {
		FluidTankInfo info = tile.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if(localId == 0 || localId == 1) {
			return (info.fluid != null && prevLiquidAmt != info.fluid.amount);
		}
		else if(localId == 2) {
			if(info.fluid == null)
				return prevLiquidUUID != invalidFluid;
			else
				return info.fluid.getFluidID() != prevLiquidUUID;
		}

		return false;
	}

	@Override
	protected void updatePreviousState(int localId) {
		FluidTankInfo info = tile.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if(localId == 0 && info.fluid != null)
			prevLiquidAmt = info.fluid.amount;
		else if(localId == 1)
			if(info.fluid == null) 
				prevLiquidUUID = invalidFluid;
			else
				prevLiquidUUID = info.fluid.getFluidID();
	}

	protected float getProgress() {
		FluidTankInfo[] info = tile.getTankInfo(ForgeDirection.UNKNOWN);

		int capacity = 0;
		int fillAmount = 0;

		for(FluidTankInfo fluidInfo : info) {
			capacity += fluidInfo.capacity;
			if(fluidInfo.fluid != null)
				fillAmount += fluidInfo.fluid.amount;
		}

		return fillAmount/(float)capacity;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderForeground (int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {

		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;
		int ySize = 52;
		int xSize = 12;

		if( relativeX > 0 && relativeX < xSize && relativeY > 0 && relativeY < ySize) {
			List<String> list = new LinkedList<String>();
			FluidStack fluidStack = tile.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;

			if(fluidStack!= null) {
				
				list.add(fluidStack.getLocalizedName()+": "+fluidStack.amount + " / " + tile.getTankInfo(ForgeDirection.UNKNOWN)[0].capacity + " mB");

				
			}
			else
				list.add("Empty");
			
			this.drawTooltip(gui, list, mouseX, mouseY, zLevel, font);
		}

	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY,  font);
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, 176, 58, 14, 54);

		//Draw Fluid
		FluidTankInfo info = tile.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if(info.fluid != null) {
			IIcon fluidIcon = info.fluid.getFluid().getIcon();

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

			int color = info.fluid.getFluid().getColor(info.fluid);

			GL11.glColor3b((byte)((color >> 16) & 127), (byte)((color >> 8) & 127), (byte)(color & 127));
			//GL11.glColor3b((byte)127, (byte)127, (byte)127);

			float percent = getProgress();
			int ySize = 52;
			int xSize = 12;
			
			if(fluidIcon != null)
				gui.drawTexturedModelRectFromIcon(offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), fluidIcon, xSize, (int)(percent*ySize));

			//this.drawProgressBarIconVertical(x + 27, y + 18, fluidIcon, 12, 52, getProgress());
		}
	}

}
