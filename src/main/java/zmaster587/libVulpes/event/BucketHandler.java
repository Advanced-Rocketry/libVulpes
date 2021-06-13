package zmaster587.libVulpes.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class BucketHandler {
	
	public static final BucketHandler INSTANCE = new BucketHandler();
	private static Map<Block, Item> bucketMap = new HashMap<>();
	private static Map<Fluid, Item> itemMap = new HashMap<>();

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		if(event.getTarget() == null || Type.BLOCK != event.getTarget().typeOfHit)
			return;
		IBlockState state =  event.getWorld().getBlockState(new BlockPos(event.getTarget().getBlockPos()));
		Block block = state.getBlock();
		Item bucket = bucketMap.get(block);
		
		if(bucket != null && state.equals(block.getDefaultState())) {
			event.getWorld().setBlockToAir(new BlockPos(event.getTarget().getBlockPos()));
			
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
