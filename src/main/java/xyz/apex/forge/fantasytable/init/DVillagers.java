package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.village.PointOfInterestType;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.util.registrate.entry.VillagerProfessionEntry;

import javax.annotation.Nullable;
import java.util.Random;

public final class DVillagers
{
	// @formatter:off
	public static final RegistryEntry<PointOfInterestType> GAMBLER_POI = FantasyTable
			.registrate()
			.object(DStrings.POI_GAMBLER)
			.pointOfInterestType()
				.matchingBlock(DBlocks.GAMBLING_TABLE::get)
				.maxTickets(1)
				.validRange(1)
			.register();

	public static final VillagerProfessionEntry<VillagerProfession> GAMBLER_PROFESSION = FantasyTable
			.registrate()
			.object(DStrings.POI_GAMBLER)
			.villagerProfession()
				.villagerTrades((profession, trades) ->trades
						.one(
								new GenericTrade(() -> Items.IRON_INGOT, 1, Coins.IRON.item::get, 3),
								new GenericTrade(() -> Items.GOLD_INGOT, 1, Coins.GOLD.item::get, 3)
						)
						.two(
								new GenericTrade(Coins.GOLD.item::get, 8, () -> Items.IRON_INGOT, 1, Dice.IRON.six_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 8, () -> Items.IRON_INGOT, 1, Dice.IRON.twenty_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 12, () -> Items.GOLD_INGOT, 1, Dice.GOLD.six_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 12, () -> Items.GOLD_INGOT, 1, Dice.GOLD.twenty_sided_die::get, 1)
						)
						.three(
								new GenericTrade(Coins.GOLD.item::get, 20, () -> Items.DIAMOND, 1, Dice.DIAMOND.six_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 20, () -> Items.DIAMOND, 1, Dice.DIAMOND.twenty_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 24, () -> Items.EMERALD, 1, Dice.EMERALD.six_sided_die::get, 1),
								new GenericTrade(Coins.GOLD.item::get, 24, () -> Items.EMERALD, 1, Dice.EMERALD.twenty_sided_die::get, 1)
						)
				)
				.defaultLang()
			.register();
	// @formatter:on

	@Deprecated // internal use only
	public static void register() { }

	private static class GenericTrade implements VillagerTrades.ITrade
	{
		private final IItemProvider costA;
		private final IItemProvider costB;
		private final IItemProvider result;

		private final int costACount;
		private final int costBCount;
		private final int resultCount;

		public GenericTrade(IItemProvider costA, int costACount, IItemProvider costB, int costBCount, IItemProvider result, int resultCount)
		{
			this.costA = costA;
			this.costB = costB;
			this.result = result;

			this.costACount = costACount;
			this.costBCount = costBCount;
			this.resultCount = resultCount;
		}

		public GenericTrade(IItemProvider cost, int costCount, IItemProvider result, int resultCount)
		{
			this(cost, costCount, () -> Items.AIR, 0, result, resultCount);
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity entity, Random rng)
		{
			// MerchantOffer(costA, costB, result, uses, maxUses, xp, priceMultiplier, demand)
			return new MerchantOffer(itemOrEmpty(costA, costACount), itemOrEmpty(costB, costBCount), itemOrEmpty(result, resultCount), 10, 4, .2F);
		}

		private ItemStack itemOrEmpty(IItemProvider itemProvider, int count)
		{
			if(count <= 0)
				return ItemStack.EMPTY;

			Item item = itemProvider.asItem();

			if(item == Items.AIR)
				return ItemStack.EMPTY;

			return new ItemStack(item, count);
		}
	}
}
