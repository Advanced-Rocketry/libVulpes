package zmaster587.libVulpes.block;

import net.minecraft.block.material.Material;
import zmaster587.libVulpes.api.ITimeModifier;

public class BlockMotor extends BlockRotatableModel implements ITimeModifier {
	
	public BlockMotor(Material par2Material, int modelId, float timeMod) {
		super(par2Material, modelId);
		this.modelID = modelId;
		this.timeModifier = timeMod;
	}
	
	float timeModifier;
	@Override
	public float getTimeMult() {
		return timeModifier;
	}
	
	/* @Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		
		tooltip.add(String.format(ChatFormatting.GRAY + "Machine Speed: %.2f", 1/getTimeMult()));
	}*/
}
