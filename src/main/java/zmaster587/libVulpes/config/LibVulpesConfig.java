package zmaster587.libVulpes.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zmaster587.libVulpes.LibVulpes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class LibVulpesConfig {

	private final static String CATEGORY_ORE = "ore_generation";
	private final static String CATEGORY_COPPER = "copper";
	private final static String CATEGORY_DILITHIUM = "dilithium";
	private final static String CATEGORY_ALUMINUM = "aluminum";
	private final static String CATEGORY_RUTILE = "rutile";

	//Only to be set in preinit
	private static LibVulpesConfig currentConfig;
	public static Logger logger = LogManager.getLogger(LibVulpes.MODID);

	public static final LibVulpesConfig common;
	private static final ForgeConfigSpec commonSpec;
	
	static {
		Pair<LibVulpesConfig, ForgeConfigSpec> commonConfiguration = new ForgeConfigSpec.Builder().configure(LibVulpesConfig::new);
		common = commonConfiguration.getLeft();
		commonSpec = commonConfiguration.getRight();
	}
	
	public static void register() {
		registerConfig(ModConfig.Type.COMMON, commonSpec, "libvulpes.toml");
	}

	private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		LibVulpes.MOD_CONTAINER.addConfig(new ModConfig(type, spec, LibVulpes.MOD_CONTAINER, fileName));
	}

	private LibVulpesConfig() { }

	public LibVulpesConfig(ForgeConfigSpec.Builder builder) {
		currentConfig = new LibVulpesConfig();

		LibVulpesConfig lvConfig = getCurrentConfig();

		builder.push(CATEGORY_ORE);
		builder.push(CATEGORY_COPPER);
		lvConfig.generateCopper = builder.define("generateCopper", true);
		lvConfig.copperClumpSize = builder.define("sizeCopper", 6);
		lvConfig.copperPerChunk = builder.define("chanceCopper", 10);
		lvConfig.copperMinHeight = builder.define("minHeightCopper", 40);
		lvConfig.copperMaxHeight = builder.define("maxHeightCopper", 120);
		builder.pop();

		builder.push(CATEGORY_DILITHIUM);
		lvConfig.generateDilithium = builder.define("generateDilithium", true);
		lvConfig.dilithiumClumpSize = builder.define("sizeDilithium", 16);
		lvConfig.dilithiumPerChunk = builder.define("chanceDilithium", 1);
		lvConfig.dilithiumMinHeight = builder.define("minHeightDilithium", 20);
		lvConfig.dilithiumMaxHeight = builder.define("maxHeightDilithium", 80);
		builder.pop();

		builder.push(CATEGORY_ALUMINUM);
		lvConfig.generateAluminum = builder.define("generateAluminum", true);
		lvConfig.aluminumClumpSize = builder.define("sizeAluminum", 16);
		lvConfig.aluminumPerChunk = builder.define("chanceAluminum", 1);
		lvConfig.aluminumMinHeight = builder.define("minHeightAluminum", 20);
		lvConfig.aluminumMaxHeight = builder.define("maxHeightAluminum", 80);
		builder.pop();

		builder.push(CATEGORY_RUTILE);
		lvConfig.generateRutile = builder.define("generateRutile", true);
		lvConfig.rutileClumpSize = builder.define("sizeRutile", 6);
		lvConfig.rutilePerChunk = builder.define("chanceRutile", 6);
		lvConfig.rutileMinHeight = builder.define("minHeightRutile", 20);
		lvConfig.rutileMaxHeight = builder.define("maxHeightRutile", 80);
		builder.pop();
		builder.pop();
	}

	public static LibVulpesConfig getCurrentConfig() {
		if(currentConfig == null) {
			logger.error("Had to generate a new config, this shouldn't happen");
			throw new NullPointerException("Expected config to not be null");
		}
		return currentConfig;
	}


	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Boolean> generateCopper;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> copperPerChunk;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> copperClumpSize;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> copperMaxHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> copperMinHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> tinMaxHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> tinMinHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> rutileMaxHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> rutileMinHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> dilithiumMaxHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> dilithiumMinHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> aluminumMaxHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> aluminumMinHeight;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Boolean> generateTin;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> tinPerChunk;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> tinClumpSize;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Boolean> generateDilithium;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> dilithiumClumpSize;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> dilithiumPerChunk;

	public ForgeConfigSpec.ConfigValue<Integer> aluminumPerChunk;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> aluminumClumpSize;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Boolean> generateAluminum;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Boolean> generateRutile;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> rutilePerChunk;

	@ConfigProperty
	public ForgeConfigSpec.ConfigValue<Integer> rutileClumpSize;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ConfigProperty {
		boolean needsSync() default false;
		Class internalType() default Object.class;
		Class keyType() default Object.class;
		Class valueType() default Object.class;
	}
}
