package zmaster587.libVulpes.tile;

import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

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
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setString("material", materialType.getUnlocalizedName());

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		materialType = MaterialRegistry.getMaterialFromName(pkt.func_148857_g().getString("material"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(materialType != null)
			nbt.setString("material", materialType.getUnlocalizedName());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("material"))
			materialType = MaterialRegistry.getMaterialFromName(nbt.getString("material"));
	}
}
