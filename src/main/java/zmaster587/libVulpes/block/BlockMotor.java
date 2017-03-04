package zmaster587.libVulpes.block;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import zmaster587.libVulpes.api.ITimeModifier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockMotor extends RotatableBlock implements ITimeModifier {
	
	float timeModifier;
	
	public BlockMotor(Material par2Material, float timeMultiplier) {
		super(par2Material);
		timeModifier = timeMultiplier;
	}
	
	@Override
	public float getTimeMult() {
		return timeModifier;
	}
	
	 @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	 
	 @Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		
		tooltip.add(String.format(ChatFormatting.GRAY + "Machine Speed: %.2f", 1/getTimeMult()));
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
			World world, BlockPos pos, EntityPlayer player) {
		return super.getPickBlock(state.getBlock().getDefaultState(), target, world, pos, player);
	}
}
