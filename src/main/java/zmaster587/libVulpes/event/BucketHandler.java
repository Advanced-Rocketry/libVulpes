package zmaster587.libVulpes.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class BucketHandler {
	
	public static final BucketHandler INSTANCE = new BucketHandler();
	private static Map<Block, Item> bucketMap = new HashMap<>();
	private static Map<Fluid, Item> itemMap = new HashMap<>();

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		if(event.getTarget() == null || Type.BLOCK != event.getTarget().getType())
			return;
		BlockState state =  event.getWorld().getBlockState(new BlockPos(event.getTarget().getHitVec()));
		Block block = state.getBlock();
		Item bucket = bucketMap.get(block);
		
		if(bucket != null && state.equals(block.getDefaultState())) {
			event.getWorld().setBlockState(new BlockPos(event.getTarget().getHitVec()), Blocks.AIR.getDefaultState());
			
			event.setFilledBucket(new ItemStack(bucket));
			
			bucket.hasContainerItem(event.getFilledBucket());
			
			event.setResult(Result.ALLOW);
		}
	}
	
	public Item getItemFromFluid(Fluid fluid)
	{
		return itemMap.get(fluid);
	}
	
	public void registerBucket(Block block, Item item, Fluid fluid) {
		bucketMap.put(block, item);
		itemMap.put(fluid, item);
	}
}
