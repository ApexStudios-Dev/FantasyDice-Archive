package xyz.apex.forge.fantasytable.util.registrate.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.RegistryObject;
import xyz.apex.forge.fantasytable.util.registrate.entry.VillagerProfessionEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class VillagerProfessionBuilder<T extends VillagerProfession, P> extends AbstractBuilder<VillagerProfession, T, P, VillagerProfessionBuilder<T, P>>
{
	private final VillagerProfessionFactory<T> factory;
	private final Set<IItemProvider> requestedItems = Sets.newHashSet();
	private final Set<NonNullSupplier<Block>> secondaryPoi = Sets.newHashSet();

	private Supplier<SoundEvent> workSound = () -> null;
	private NonNullSupplier<PointOfInterestType> poiType = () -> PointOfInterestType.UNEMPLOYED;
	private NonNullBiConsumer<T, VillagerTradesRegistrar<T>> villagerTradesConsumer = NonNullBiConsumer.noop();
	private NonNullBiConsumer<T, WandererTradesRegistrar<T>> wandererTradesConsumer = NonNullBiConsumer.noop();

	protected VillagerProfessionBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, VillagerProfessionFactory<T> factory)
	{
		super(owner, parent, name, callback, VillagerProfession.class);

		this.factory = factory;

		onRegister(profession -> {
			VillagerTradesRegistrar<T> villagerTradesRegistrar = new VillagerTradesRegistrar<>(profession);
			WandererTradesRegistrar<T> wandererTradesRegistrar = new WandererTradesRegistrar<>(profession);

			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, VillagerTradesEvent.class, event -> villagerTradesRegistrar.onEvent(event, villagerTradesConsumer));
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, WandererTradesEvent.class, event -> wandererTradesRegistrar.onEvent(event, wandererTradesConsumer));
		});
	}

	// region: POI
	public VillagerProfessionBuilder<T, P> pointOfInterestType(NonNullSupplier<PointOfInterestType> poiType)
	{
		this.poiType = poiType;
		return this;
	}

	// region: Secondary POI
	public VillagerProfessionBuilder<T, P> secondaryPoi(NonNullSupplier<Block> secondaryPoi)
	{
		this.secondaryPoi.add(secondaryPoi);
		return this;
	}

	public VillagerProfessionBuilder<T, P> secondaryPois(NonNullSupplier<Block>... secondaryPois)
	{
		Arrays.stream(secondaryPois).forEach(secondaryPoi::add);
		return this;
	}
	// endregion
	// endregion

	// region: Requested Items
	public VillagerProfessionBuilder<T, P> requestItem(IItemProvider item)
	{
		requestedItems.add(item);
		return this;
	}

	public VillagerProfessionBuilder<T, P> requestItems(IItemProvider... items)
	{
		Arrays.stream(items).forEach(requestedItems::add);
		return this;
	}
	// endregion

	public VillagerProfessionBuilder<T, P> villagerTrades(NonNullBiConsumer<T, VillagerTradesRegistrar<T>> villagerTradesConsumer)
	{
		this.villagerTradesConsumer = villagerTradesConsumer;
		return this;
	}

	public VillagerProfessionBuilder<T, P> wandererTrades(NonNullBiConsumer<T, WandererTradesRegistrar<T>> wandererTradesConsumer)
	{
		this.wandererTradesConsumer = wandererTradesConsumer;
		return this;
	}

	public VillagerProfessionBuilder<T, P> workSound(NonNullSupplier<SoundEvent> workSound)
	{
		this.workSound = workSound;
		return this;
	}

	/**
	 * Return correct language key desginated by {@link VillagerEntity#getTypeName()}<br>
	 * Example {@code entity.minecraft.villager.[<mod_id>].<profession_name>}
	 */
	private String getDefaultLanguageKey()
	{
		return String.format("%s.%s.%s", EntityType.VILLAGER.getDescriptionId(), getOwner().getModid(), getName());
	}

	public VillagerProfessionBuilder<T, P> defaultLang()
	{
		return lang(profession -> getDefaultLanguageKey());
	}

	public VillagerProfessionBuilder<T, P> lang(String name)
	{
		return lang(profession -> getDefaultLanguageKey(), name);
	}

	@Override
	protected RegistryEntry<T> createEntryWrapper(RegistryObject<T> delegate)
	{
		return new VillagerProfessionEntry<>(getOwner(), delegate);
	}

	@Override
	protected @NonnullType T createEntry()
	{
		String registryName = String.format("%s:%s", getOwner().getModid(), getName());
		PointOfInterestType poiType = this.poiType.get();
		ImmutableSet<Item> requestedItems = this.requestedItems.stream().map(IItemProvider::asItem).filter(item -> item != Items.AIR).collect(ImmutableSet.toImmutableSet());
		ImmutableSet<Block> secondaryPoi = this.secondaryPoi.stream().map(Supplier::get).filter(block -> block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR).collect(ImmutableSet.toImmutableSet());
		SoundEvent workSound = this.workSound.get();
		return factory.create(registryName, poiType, requestedItems, secondaryPoi, workSound);
	}

	@Override
	public VillagerProfessionEntry<T> register()
	{
		return (VillagerProfessionEntry<T>) super.register();
	}

	public static <T extends VillagerProfession, P> VillagerProfessionBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, VillagerProfessionFactory<T> factory)
	{
		return new VillagerProfessionBuilder<>(owner, parent, name, callback, factory).defaultLang();
	}

	@FunctionalInterface
	public interface VillagerProfessionFactory<T extends VillagerProfession>
	{
		T create(String name, PointOfInterestType poiType, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound);
	}

	public enum TradeLevel
	{
		ONE, TWO, THREE, FOUR, FIVE, SIX;

		public static final TradeLevel[] LEVELS = values();

		public int getLevel()
		{
			return ordinal();
		}

		public static TradeLevel from(int level)
		{
			return level <= 5 && level >= 0 ? LEVELS[level] : ONE;
		}
	}

	public static final class VillagerTradesRegistrar<T extends VillagerProfession>
	{
		private final Map<TradeLevel, Set<VillagerTrades.ITrade>> tradeMap = Maps.newEnumMap(TradeLevel.class);
		private final T villagerProfession;

		private VillagerTradesRegistrar(T villagerProfession)
		{
			this.villagerProfession = villagerProfession;
		}

		void onEvent(VillagerTradesEvent event, NonNullBiConsumer<T, VillagerTradesRegistrar<T>> consumer)
		{
			if(event.getType() == villagerProfession)
			{
				consumer.accept(villagerProfession, this);

				if(!tradeMap.isEmpty())
				{
					Int2ObjectMap<List<VillagerTrades.ITrade>> trades = event.getTrades();
					tradeMap.forEach((key, value) -> trades.put(key.getLevel(), ImmutableList.copyOf(value)));
					tradeMap.clear();
				}
			}
		}

		// region: Generic
		public VillagerTradesRegistrar<T> register(TradeLevel level, VillagerTrades.ITrade trade)
		{
			tradeMap.computeIfAbsent(level, $ -> Sets.newHashSet()).add(trade);
			return this;
		}

		public VillagerTradesRegistrar<T> register(TradeLevel level, VillagerTrades.ITrade... trades)
		{
			Set<VillagerTrades.ITrade> tradeSet = tradeMap.computeIfAbsent(level, $ -> Sets.newHashSet());
			Collections.addAll(tradeSet, trades);
			return this;
		}

		public VillagerTradesRegistrar<T> register(TradeLevel level, Collection<VillagerTrades.ITrade> trades)
		{
			tradeMap.computeIfAbsent(level, $ -> Sets.newHashSet()).addAll(trades);
			return this;
		}
		// endregion

		// region: Levels
		// region: Level One
		public VillagerTradesRegistrar<T> one(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.ONE, trade);
		}

		public VillagerTradesRegistrar<T> one(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.ONE, trades);
		}

		public VillagerTradesRegistrar<T> one(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.ONE, trades);
		}
		// endregion

		// region: Level Two
		public VillagerTradesRegistrar<T> two(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.TWO, trade);
		}

		public VillagerTradesRegistrar<T> two(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.TWO, trades);
		}

		public VillagerTradesRegistrar<T> two(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.TWO, trades);
		}
		// endregion

		// region: Level Three
		public VillagerTradesRegistrar<T> three(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.THREE, trade);
		}

		public VillagerTradesRegistrar<T> three(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.THREE, trades);
		}

		public VillagerTradesRegistrar<T> three(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.THREE, trades);
		}
		// endregion

		// region: Level Four
		public VillagerTradesRegistrar<T> four(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.FOUR, trade);
		}

		public VillagerTradesRegistrar<T> four(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.FOUR, trades);
		}

		public VillagerTradesRegistrar<T> four(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.FOUR, trades);
		}
		// endregion

		// region: Level Five
		public VillagerTradesRegistrar<T> five(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.FIVE, trade);
		}

		public VillagerTradesRegistrar<T> five(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.FIVE, trades);
		}

		public VillagerTradesRegistrar<T> five(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.FIVE, trades);
		}
		// endregion

		// region: Level Six
		public VillagerTradesRegistrar<T> six(VillagerTrades.ITrade trade)
		{
			return register(TradeLevel.SIX, trade);
		}

		public VillagerTradesRegistrar<T> six(VillagerTrades.ITrade... trades)
		{
			return register(TradeLevel.SIX, trades);
		}

		public VillagerTradesRegistrar<T> six(Collection<VillagerTrades.ITrade> trades)
		{
			return register(TradeLevel.SIX, trades);
		}
		// endregion
		// endregion
	}

	public static final class WandererTradesRegistrar<T extends VillagerProfession>
	{
		private final Set<VillagerTrades.ITrade> genericTrades = Sets.newHashSet();
		private final Set<VillagerTrades.ITrade> rareTrades = Sets.newHashSet();
		private final T villagerProfession;

		private WandererTradesRegistrar(T villagerProfession)
		{
			this.villagerProfession = villagerProfession;
		}

		void onEvent(WandererTradesEvent event, NonNullBiConsumer<T, WandererTradesRegistrar<T>> consumer)
		{
			consumer.accept(villagerProfession, this);

			if(!genericTrades.isEmpty())
			{
				event.getGenericTrades().addAll(genericTrades);
				genericTrades.clear();
			}

			if(!rareTrades.isEmpty())
			{
				event.getRareTrades().addAll(rareTrades);
				rareTrades.clear();
			}
		}

		// region: Trades
		public WandererTradesRegistrar<T> register(boolean rare, VillagerTrades.ITrade trade)
		{
			(rare ? rareTrades : genericTrades).add(trade);
			return this;
		}

		public WandererTradesRegistrar<T> register(boolean rare, VillagerTrades.ITrade... trades)
		{
			Collections.addAll(rare ? rareTrades : genericTrades, trades);
			return this;
		}

		public WandererTradesRegistrar<T> register(boolean rare, Collection<VillagerTrades.ITrade> trades)
		{
			(rare ? rareTrades : genericTrades).addAll(trades);
			return this;
		}

		// region: Generic Trades
		public WandererTradesRegistrar<T> generic(VillagerTrades.ITrade trade)
		{
			return register(false, trade);
		}

		public WandererTradesRegistrar<T> generic(VillagerTrades.ITrade... trades)
		{
			return register(false, trades);
		}

		public WandererTradesRegistrar<T> generic(Collection<VillagerTrades.ITrade> trades)
		{
			return register(false, trades);
		}
		// endregion

		// region: Rare Trades
		public WandererTradesRegistrar<T> rare(VillagerTrades.ITrade trade)
		{
			return register(true, trade);
		}

		public WandererTradesRegistrar<T> rare(VillagerTrades.ITrade... trades)
		{
			return register(true, trades);
		}

		public WandererTradesRegistrar<T> rare(Collection<VillagerTrades.ITrade> trades)
		{
			return register(true, trades);
		}
		// endregion
		// endregion
	}
}
