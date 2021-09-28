package xyz.apex.forge.fantasytable.init;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.util.DeferredVillagerProfession;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class DVillagers
{
	public static final DeferredRegister<PointOfInterestType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, FantasyTable.ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, FantasyTable.ID);
	public static final DeferredRegister<Structure<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, FantasyTable.ID);

	public static final RegistryObject<PointOfInterestType> GAMBLER_POI = POI_TYPES.register(DStrings.POI_GAMBLER, () -> new PointOfInterestType(FantasyTable.ID + ':' + DStrings.POI_GAMBLER, PointOfInterestType.getBlockStates(DBlocks.GAMBLING_TABLE.get()), 1, 1));
	public static final RegistryObject<VillagerProfession> GAMBLER_PROFESSION = PROFESSIONS.register(DStrings.POI_GAMBLER, () -> DeferredVillagerProfession.create(GAMBLER_POI, null));

	@Deprecated // internal use only
	public static void register()
	{
		POI_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		PROFESSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
		STRUCTURE_FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());

		MinecraftForge.EVENT_BUS.addListener(DVillagers::onRegisterVillagerTrades);
	}

	private static void onRegisterVillagerTrades(VillagerTradesEvent event)
	{
		if(event.getType() != GAMBLER_PROFESSION.get())
			return;

		Int2ObjectMap<List<VillagerTrades.ITrade>> trades = event.getTrades();

		trades.put(1, ImmutableList.of(
				new GenericTrade(() -> Items.IRON_INGOT, 1, Coins.IRON.item::get, 3),
				new GenericTrade(() -> Items.GOLD_INGOT, 1, Coins.GOLD.item::get, 3)
		));

		trades.put(2, ImmutableList.of(
				new GenericTrade(Coins.GOLD.item::get, 8, () -> Items.IRON_INGOT, 1, Dice.IRON.six_sided_die::get, 1),
				new GenericTrade(Coins.GOLD.item::get, 8, () -> Items.IRON_INGOT, 1, Dice.IRON.twenty_sided_die::get, 1),

				new GenericTrade(Coins.GOLD.item::get, 12, () -> Items.GOLD_INGOT, 1, Dice.GOLD.six_sided_die::get, 1),
				new GenericTrade(Coins.GOLD.item::get, 12, () -> Items.GOLD_INGOT, 1, Dice.GOLD.twenty_sided_die::get, 1)
		));

		trades.put(3, ImmutableList.of(
				new GenericTrade(Coins.GOLD.item::get, 20, () -> Items.DIAMOND, 1, Dice.DIAMOND.six_sided_die::get, 1),
				new GenericTrade(Coins.GOLD.item::get, 20, () -> Items.DIAMOND, 1, Dice.DIAMOND.twenty_sided_die::get, 1),

				new GenericTrade(Coins.GOLD.item::get, 24, () -> Items.EMERALD, 1, Dice.EMERALD.six_sided_die::get, 1),
				new GenericTrade(Coins.GOLD.item::get, 24, () -> Items.EMERALD, 1, Dice.EMERALD.twenty_sided_die::get, 1)
		));

		trades.put(4, ImmutableList.of());
		trades.put(5, ImmutableList.of());
		trades.put(6, ImmutableList.of());
		// @formatter:on
	}

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
