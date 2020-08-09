package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;

public class TileMaterial extends TilePointer {

	Material materialType;

	public TileMaterial(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	public Material getMaterial() {
		return materialType;
	}

	public void setMaterial(Material material) {
		materialType = material;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();

		nbt.putString("material", materialType.getUnlocalizedName());

		return new SUpdateTileEntityPacket(pos, -1, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putString("material", materialType.getUnlocalizedName());
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		materialType = MaterialRegistry.getMaterialFromName(tag.getString("material"));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		materialType = MaterialRegistry.getMaterialFromName(pkt.getNbtCompound().getString("material"));
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if(materialType != null)
			nbt.putString("material", materialType.getUnlocalizedName());
		
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);
		if(nbt.contains("material"))
			materialType = MaterialRegistry.getMaterialFromName(nbt.getString("material"));
	}
}
