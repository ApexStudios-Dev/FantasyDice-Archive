package xyz.apex.forge.fantasydice;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import xyz.apex.forge.apexcore.lib.util.ForgeEventBusHelper;
import xyz.apex.forge.fantasydice.command.RollCommand;
import xyz.apex.forge.fantasydice.init.FTRegistry;

import java.util.UUID;

@Mod(FantasyDice.ID)
public final class FantasyDice
{
	public static final String ID = "fantasydice";

	public static final Config CONFIG;
	private static final ForgeConfigSpec CONFIG_SPEC;

	public static final String DIE_ROLL_KEY = ID + ".die.roll";
	public static final String DIE_ROLL_RESULT_KEY = ID + ".die.roll.result";
	public static final String DIE_ROLL_DESC_KEY = ID + ".die.roll.desc";

	public static final String DIE_APEX_NAME = ID + ".die.apex.name";

	public static final UUID FANTASY_UUID = UUID.fromString("598535bd-f330-4123-b4d0-c6e618390477");

	static
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		CONFIG = new Config(builder);
		CONFIG_SPEC = builder.build();
	}

	public FantasyDice()
	{
		FTRegistry.bootstrap();
		ForgeEventBusHelper.addListener(RegisterCommandsEvent.class, event -> RollCommand.register(event.getDispatcher()));
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC, ID + ".toml");
	}

	public static final class Config
	{
		public final ForgeConfigSpec.IntValue diceRollMessageRange;
		public final ForgeConfigSpec.BooleanValue diceRollMessageCrossDimensions;

		private Config(ForgeConfigSpec.Builder builder)
		{
			diceRollMessageRange = builder
					.comment("Range from Dice Thrower Players must be within to see the Roll messages", "Note: Range is in Chunks using Chessboard Distancing")
					.defineInRange("die.roll_message.range", 4, 1, Integer.MAX_VALUE);

			diceRollMessageCrossDimensions = builder
					.comment("Whether or not Dice Roll messages will be displayed to Players in different Dimensions than the Thrower", "Note: If this is True the Roll Range will be ignored in differing Dimensions")
					.define("die.roll_message.cross_dimension", false);
		}
	}
}
