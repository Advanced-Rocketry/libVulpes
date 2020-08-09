package zmaster587.libVulpes.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CommonConfig {
	
	public static ConfigValue<Double> EUMult;
	public static ConfigValue<Double> powerMult;
	
	CommonConfig(ForgeConfigSpec.Builder builder) 
	{

		
		builder.push("Power");
		
		EUMult = builder.comment("\"How many power unit one EU makes\"")
		.define("EUPowerMultiplier", 7.0);
		
		powerMult = builder.comment("Power multiplier on machines")
				.define("PowerMultiplier", 1.0);
	}
}
