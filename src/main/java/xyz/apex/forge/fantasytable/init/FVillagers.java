package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Items;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.BasicTrade;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.util.DiceHelper;
import xyz.apex.forge.fantasytable.util.registrate.entry.VillagerProfessionEntry;

public final class FVillagers
{
	// @formatter:off
	public static final RegistryEntry<PointOfInterestType> GAMBLER_POI = FantasyTable
			.registrate()
			.object(FStrings.POI_GAMBLER)
			.pointOfInterestType()
				.matchingBlock(FBlocks.GAMBLING_TABLE)
				.maxTickets(1)
				.validRange(1)
			.register();

	public static final VillagerProfessionEntry<VillagerProfession> GAMBLER_PROFESSION = FantasyTable
			.registrate()
			.object(FStrings.POI_GAMBLER)
			.villagerProfession()
				.pointOfInterestType(GAMBLER_POI)
				.villagerTrades((profession, trades) -> {
					// new BasicTrade(price1, price2, result, maxTrades, xp, priceMult)
					// new BasicTrade(price1, result, maxTrades, xp, priceMult)

					// tier 1 trades
					trades.one(
							// ingot -> coin
							new BasicTrade(Items.IRON_INGOT.getDefaultInstance(), Coins.IRON.item.asStack(), 20, 10, 1F),
							new BasicTrade(Items.GOLD_INGOT.getDefaultInstance(), Coins.GOLD.item.asStack(), 20, 10, 1F),

							// gold coin x 4 + iron ingot -> iron dice
							new BasicTrade(Coins.GOLD.item.asStack(4), Items.IRON_INGOT.getDefaultInstance(), Dice.IRON.six_sided_die.asStack(), 10, 10, 1F),
							new BasicTrade(Coins.GOLD.item.asStack(4), Items.IRON_INGOT.getDefaultInstance(), Dice.IRON.twenty_sided_die.asStack(), 10, 10, 1F)
					);

					// tier 2 trades
					trades.two(
							// gold coin x 8 + gold ingot -> gold dice
							new BasicTrade(Coins.GOLD.item.asStack(8), Items.GOLD_INGOT.getDefaultInstance(), Dice.GOLD.six_sided_die.asStack(), 10, 15, 1F),
							new BasicTrade(Coins.GOLD.item.asStack(8), Items.GOLD_INGOT.getDefaultInstance(), Dice.GOLD.twenty_sided_die.asStack(), 10, 15, 1F)
					);

					// tier 3 trades
					trades.three(
							// gold coin x 12 + diamond -> diamond dice
							new BasicTrade(Coins.GOLD.item.asStack(12), Items.DIAMOND.getDefaultInstance(), Dice.DIAMOND.six_sided_die.asStack(), 10, 20, 1F),
							new BasicTrade(Coins.GOLD.item.asStack(12), Items.DIAMOND.getDefaultInstance(), Dice.DIAMOND.twenty_sided_die.asStack(), 10, 20, 1F)
					);

					// tier 4 trades
					trades.four(
							// gold coin x 16 + emerald -> emerald dice
							new BasicTrade(Coins.GOLD.item.asStack(16), Items.EMERALD.getDefaultInstance(), Dice.EMERALD.six_sided_die.asStack(), 10, 25, 1F),
							new BasicTrade(Coins.GOLD.item.asStack(16), Items.EMERALD.getDefaultInstance(), Dice.EMERALD.twenty_sided_die.asStack(), 10, 25, 1F)
					);

					// tier 5 trades
					for(Dice dice : Dice.TYPES)
					{
						trades.five(
								new BasicTrade(Coins.EMERALD.item.asStack(), dice.six_sided_die.asStack(), DiceHelper.setLoadedState(dice.six_sided_die.asStack(), true), 5, 30, .7F),
								new BasicTrade(Coins.EMERALD.item.asStack(), dice.twenty_sided_die.asStack(), DiceHelper.setLoadedState(dice.twenty_sided_die.asStack(), true), 5, 30, .7F)
						);
					}
				})
				.defaultLang()
			.register();
	// @formatter:on

	@Deprecated // internal use only
	public static void register() { }
}
