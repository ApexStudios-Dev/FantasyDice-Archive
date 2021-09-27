package xyz.apex.forge.fantasytable.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ServerConfig
{
	public final ForgeConfigSpec spec;

	private final ForgeConfigSpec.IntValue diceCooldown;
	private final ForgeConfigSpec.IntValue diceRollMessageRange;

	public ServerConfig()
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		// formatter:off
		diceCooldown = builder
				.comment("Cooldown for dice items (value is in TPS, 20 == 1 second)")
				.defineInRange("dice.cooldown", 20, 0, Integer.MAX_VALUE);

		diceRollMessageRange = builder
				.comment("The range in which players can see roll messages (0 means everyone can see the message)")
				.defineInRange("dice.rollMsgRange", 10, 0, Integer.MAX_VALUE);
		// formatter:on

		spec = builder.build();
	}

	public int getDiceCooldown()
	{
		return diceCooldown.get();
	}

	public int getDiceRollRange()
	{
		return diceRollMessageRange.get();
	}
}
