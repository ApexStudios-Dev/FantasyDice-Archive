package xyz.apex.forge.fantasydice;

import com.google.common.collect.Lists;

import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import xyz.apex.forge.apexcore.lib.util.ForgeEventBusHelper;
import xyz.apex.forge.apexcore.lib.util.ModEventBusHelper;
import xyz.apex.forge.fantasydice.command.RollCommand;
import xyz.apex.forge.fantasydice.init.DiceType;
import xyz.apex.forge.fantasydice.init.FTRegistry;
import xyz.apex.forge.utility.registrator.entry.ItemEntry;

import java.util.Collections;
import java.util.List;
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

	public static final String JEI_DICE_RECIPE_TITLE_KEY = ID + ".jei.dice_recipe.name";

	public static final UUID FANTASY_UUID = UUID.fromString("598535bd-f330-4123-b4d0-c6e618390477");
	public static boolean loadComplete = false;

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
		ModEventBusHelper.addListener(ModConfig.ModConfigEvent.class, CONFIG::onConfigReload);
		ModEventBusHelper.addListener(EventPriority.LOWEST, FMLLoadCompleteEvent.class, event -> loadComplete = true);
		ForgeEventBusHelper.addListener(WandererTradesEvent.class, this::onWandererTrades);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC, ID + ".toml");
	}

	private void onWandererTrades(WandererTradesEvent event)
	{
		List<VillagerTrades.ITrade> rareTrades = event.getRareTrades();

		for(DiceType<?, ?> diceType : DiceType.getDiceTypes())
		{
			if(diceType.getType() == DiceType.Type.SPECIALITY)
			{
				for(ItemEntry<?> item : diceType.getItems())
				{
					rareTrades.add(new BasicTrade(6, item.asItemStack(), 10, 10));
				}
			}
		}
	}

	public static final class Config
	{
		public final ForgeConfigSpec.IntValue diceRollMessageRange;
		public final ForgeConfigSpec.BooleanValue diceRollMessageCrossDimensions;

		public final ForgeConfigSpec.IntValue diceWoodenQuality;
		public final ForgeConfigSpec.IntValue diceStoneQuality;
		public final ForgeConfigSpec.IntValue diceBoneQuality;
		public final ForgeConfigSpec.IntValue dicePaperQuality;
		public final ForgeConfigSpec.IntValue diceIronQuality;
		public final ForgeConfigSpec.IntValue diceGoldenQuality;
		public final ForgeConfigSpec.IntValue diceDiamondQuality;
		public final ForgeConfigSpec.IntValue diceEmeraldQuality;
		public final ForgeConfigSpec.IntValue diceNetheriteQuality;

		private final ForgeConfigSpec.ConfigValue<List<? extends String>> diceLuckyRollers;
		public final List<UUID> luckyRollerIDs = Lists.newArrayList();

		private Config(ForgeConfigSpec.Builder builder)
		{
			diceRollMessageRange = builder
					.comment("Range from Dice Thrower Players must be within to see the Roll messages", "Note: Range is in Chunks using Chessboard Distancing")
					.defineInRange("die.roll_message.range", 4, 1, Integer.MAX_VALUE);

			diceRollMessageCrossDimensions = builder
					.comment("Whether or not Dice Roll messages will be displayed to Players in different Dimensions than the Thrower", "Note: If this is True the Roll Range will be ignored in differing Dimensions")
					.define("die.roll_message.cross_dimension", false);

			diceWoodenQuality = builder
					.comment("Quality of 'Wooden Dice' rolls")
					.defineInRange("die.quality.wooden", -3, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceStoneQuality = builder
					.comment("Quality of 'Stone Dice' rolls")
					.defineInRange("die.quality.stone", -2, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceBoneQuality = builder
					.comment("Quality of 'Bone Dice' rolls")
					.defineInRange("die.quality.bone", -1, Integer.MIN_VALUE, Integer.MAX_VALUE);

			dicePaperQuality = builder
					.comment("Quality of 'Paper Dice' rolls")
					.defineInRange("die.quality.paper", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceIronQuality = builder
					.comment("Quality of 'Iron Dice' rolls")
					.defineInRange("die.quality.iron", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceGoldenQuality = builder
					.comment("Quality of 'Golden Dice' rolls")
					.defineInRange("die.quality.golden", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceDiamondQuality = builder
					.comment("Quality of 'Diamond Dice' rolls")
					.defineInRange("die.quality.diamond", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceEmeraldQuality = builder
					.comment("Quality of 'Emerald Dice' rolls")
					.defineInRange("die.quality.emerald", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceNetheriteQuality = builder
					.comment("Quality of 'Netherite Dice' rolls")
					.defineInRange("die.quality.netherite", 4, Integer.MIN_VALUE, Integer.MAX_VALUE);

			diceLuckyRollers = builder
					.comment("List of player profile UUID's that are considered Lucky Rollers for 'Fantasy's Lucky Dice'", "Example: '43fd393b-879d-45ec-b2d5-ce8c4688ab66' - Would be for 'ApexSPG' (Values must include dashes '-')", "Note: Use somewhere like 'https://mcuuid.net/' to obtain profile UUID's")
					.defineList("die.lucky_rollers", Collections.emptyList(), this::isValidUUID);
		}

		@SuppressWarnings("ResultOfMethodCallIgnored")
		private boolean isValidUUID(Object obj)
		{
			if(obj instanceof String)
			{
				String str = (String) obj;

				try
				{
					UUID.fromString(str);
					return true;
				}
				catch(IllegalArgumentException e)
				{
					return false;
				}
			}

			return false;
		}

		private void onConfigReload(ModConfig.ModConfigEvent event)
		{
			ModConfig config = event.getConfig();

			if(config.getType() == ModConfig.Type.COMMON && config.getModId().equals(ID))
			{
				luckyRollerIDs.clear();
				diceLuckyRollers.get().stream().map(UUID::fromString).forEach(luckyRollerIDs::add);
			}
		}
	}
}
