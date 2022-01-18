package zmaster587.libVulpes.block.multiblock;

import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.tile.energy.TilePlugBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.libVulpes.tile.IComparatorOverride;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockMultiMachineBattery extends BlockMultiblockStructure {

	protected Class<? extends TileEntity> tileClass;
	protected GuiHandler.guiId guiId;
	
	public BlockMultiMachineBattery(Properties properties, Class<? extends TilePlugBase> tileClass, GuiHandler.guiId guiId) {
		super(properties);
		this.tileClass = tileClass;
		this.guiId = guiId;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	@Nonnull
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile != null && !worldIn.isRemote) {
			if(tile instanceof IModularInventory)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tile, buf -> {buf.writeInt(((IModularInventory)tile).getModularInvType().ordinal()); buf.writeBlockPos(pos); });
		}
		return ActionResultType.SUCCESS;
	}

	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		try {
			TilePlugBase tile = (TilePlugBase) tileClass.newInstance();
			tile.setTeir(1);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof IComparatorOverride) {
			return ((IComparatorOverride) tile).getComparatorOverride();
		}
		return 0;
	}
}
