package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface ISidedRedstoneTile {
	boolean allowRedstoneOutputOnSide(Direction direction);
}
