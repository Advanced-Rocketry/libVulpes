package zmaster587.libVulpes.api;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiModular;
import zmaster587.libVulpes.inventory.GuiModularFullScreen;

public class LibvulpesGuiRegistry {
	
	public static ContainerType<ContainerModular> CONTAINER_MODULAR_HELD_ITEM;
	public static ContainerType<ContainerModular> CONTAINER_MODULAR_TILE;
	public static ContainerType<ContainerModular> CONTAINER_MODULAR_ENTITY;
	
	
	public static void initContainers(RegistryEvent.Register<ContainerType<?>> evt)
	{
    	CONTAINER_MODULAR_HELD_ITEM = IForgeContainerType.create(ContainerModular::createFromNetworkItem);
    	CONTAINER_MODULAR_TILE = IForgeContainerType.create(ContainerModular::createFromNetworkBlock);
    	CONTAINER_MODULAR_ENTITY = IForgeContainerType.create(ContainerModular::createFromEntity);
    	
    	
    	evt.getRegistry().registerAll(
    			CONTAINER_MODULAR_HELD_ITEM.setRegistryName("modular_held_item"), 
    			CONTAINER_MODULAR_TILE.setRegistryName("modular_tile"),
    			CONTAINER_MODULAR_ENTITY.setRegistryName("modular_entity"));
    	
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			ScreenManager.registerFactory(CONTAINER_MODULAR_HELD_ITEM, GuiModular::new);
			ScreenManager.registerFactory(CONTAINER_MODULAR_TILE, GuiModular::new);
			ScreenManager.registerFactory(CONTAINER_MODULAR_ENTITY, GuiModular::new);
		});
	}
}
