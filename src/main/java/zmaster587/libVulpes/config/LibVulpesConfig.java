package zmaster587.libVulpes.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import zmaster587.libVulpes.LibVulpes;

public class LibVulpesConfig {

	
	public static final CommonConfig common;
	private static final ForgeConfigSpec commonSpec;
	
	static {
		Pair<CommonConfig, ForgeConfigSpec> commonConfiguration = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		common = commonConfiguration.getLeft();
		commonSpec = commonConfiguration.getRight();
	}
	
	public static void register() {
		registerConfig(ModConfig.Type.COMMON, commonSpec, "libvulpes.toml");
	}

	private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		LibVulpes.MOD_CONTAINER.addConfig(new ModConfig(type, spec, LibVulpes.MOD_CONTAINER, fileName));
	}
}
