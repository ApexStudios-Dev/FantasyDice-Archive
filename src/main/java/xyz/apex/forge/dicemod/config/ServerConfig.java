package xyz.apex.forge.dicemod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ServerConfig
{
	public final ForgeConfigSpec spec;

	private final ForgeConfigSpec.IntValue diceCooldown;

	public ServerConfig()
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		// formatter:off
		diceCooldown = builder
				.comment("Cooldown for dice items (value is in TPS, 20 == 1 second)")
				.defineInRange("dice.cooldown", 20, 0, Integer.MAX_VALUE);
		// formatter:on

		spec = builder.build();
	}

	public int getDiceCooldown()
	{
		return diceCooldown.get();
	}
}
