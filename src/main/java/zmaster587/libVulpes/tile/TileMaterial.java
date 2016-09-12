package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileMaterial extends TilePointer {

	Material materialType;

	public TileMaterial() {
		super();
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setString("material", materialType.getUnlocalizedName());

		return new SPacketUpdateTileEntity(pos, worldObj.provider.getDimension(), nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		materialType = MaterialRegistry.getMaterialFromName(pkt.getNbtCompound().getString("material"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(materialType != null)
			nbt.setString("material", materialType.getUnlocalizedName());
		
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("material"))
			materialType = MaterialRegistry.getMaterialFromName(nbt.getString("material"));
	}
}
