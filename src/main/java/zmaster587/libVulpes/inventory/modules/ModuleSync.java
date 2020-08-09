package zmaster587.libVulpes.inventory.modules;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;

public class ModuleSync extends ModuleBase {

	IDataSync tile;
	int id, prevData;
	public ModuleSync(int id, IDataSync tile) {
		super(0, 0);
		this.tile = tile;
		this.id = id;
		prevData = -1;
	}
	
	@Override
	public boolean needsUpdate(int localId) {
		return tile.getData(id) != prevData;
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		prevData = tile.getData(id);
	}
	
	@Override
	public void onChangeRecieved(int slot, int value) {
		tile.setData(id, value);
	}
	
	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		crafter.sendWindowProperty(container, variableId, tile.getData(id));
	}
	
	@Override
	public int numberOfChangesToSend() {
		return 1;
	}
}
