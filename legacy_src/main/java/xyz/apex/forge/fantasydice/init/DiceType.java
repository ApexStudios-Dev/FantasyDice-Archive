package xyz.apex.forge.fantasydice.init;

import com.google.common.collect.Lists;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import xyz.apex.forge.apexcore.registrate.CoreRegistrate;
import xyz.apex.forge.apexcore.registrate.builder.ItemBuilder;
import xyz.apex.forge.apexcore.registrate.entry.ItemEntry;
import xyz.apex.forge.apexcore.registrate.holder.ItemHolder;
import xyz.apex.forge.commonality.Mods;
import xyz.apex.forge.commonality.tags.ItemTags;
import xyz.apex.forge.fantasydice.item.DiceItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;

public final class DiceType<OWNER extends CoreRegistrate<OWNER> & ItemHolder<OWNER>, DIE extends DiceItem>
{
	private static final List<DiceType<?, ?>> diceTypes = Lists.newArrayList();

	private final String name;
	private final OWNER owner;
	private final Int2ObjectMap<ItemEntry<DIE>> diceItems = new Int2ObjectOpenHashMap<>();
	private final TagKey<Item> tag;
	private final BiFunction<ItemStack, Style, Style> styleModifier;
	private final IntSupplier diceQuality;
	private final RollCallback rollCallback;
	private final boolean usesFoil;
	private final Type type;

	private DiceType(Builder<OWNER, DIE> builder)
	{
		name = Validate.notBlank(builder.name);
		owner = builder.owner;
		tag = builder.tag;
		styleModifier = builder.styleModifier;
		rollCallback = builder.rollCallback;
		usesFoil = builder.usesFoil;
		diceQuality = builder.diceQuality;
		type = builder.type;

		for(var entry : builder.dieNames.int2ObjectEntrySet())
		{
			var sides = entry.getIntKey();
			var dieName = entry.getValue();

			RegistryEntry<DIE> registryEntry = owner.get(dieName, Registries.ITEM);
			var itemEntry = ItemEntry.cast(registryEntry);

			diceItems.put(sides, itemEntry);
		}

		diceTypes.add(this);

		owner.addRegisterCallback(Registries.ITEM, () -> diceItems
				.values()
				.stream()
				.map(RegistryEntry::get)
				.forEach(i->i.setDiceType(this))
		);
	}

	public boolean matches(DiceType<?, ?> other)
	{
		return owner.getModId().equals(other.owner.getModId()) && name.equals(other.name);
	}

	public OWNER getOwner()
	{
		return owner;
	}

	public Style withStyle(ItemStack stack, Style style)
	{
		return styleModifier.apply(stack, style);
	}

	public TagKey<Item> getTag()
	{
		return tag;
	}

	// throws exception if called with invalid dice type
	// must `withDice()` with correct side count for this to work
	public ItemEntry<DIE> getItem(int sides)
	{
		if(diceItems.containsKey(sides))
			return diceItems.get(sides);

		throw new NullPointerException("Unknown Die side count: " + sides);
	}

	public Type getType()
	{
		return type;
	}

	public ObjectCollection<ItemEntry<DIE>> getItems()
	{
		return diceItems.values();
	}

	public int getDiceQuality()
	{
		return diceQuality.getAsInt();
	}

	public IntSet getValidSides()
	{
		return diceItems.keySet();
	}

	public String getName()
	{
		return name;
	}

	public boolean usesFoil()
	{
		return usesFoil;
	}

	public int onRoll(Player player, InteractionHand hand, ItemStack stack, int min, int sides, int roll)
	{
		var diceQuality = this.diceQuality.getAsInt();
		return rollCallback.onRoll(player, hand, stack, min, sides, roll, diceQuality);
	}

	public static <OWNER extends CoreRegistrate<OWNER> & ItemHolder<OWNER>, DIE extends DiceItem> Builder<OWNER, DIE> builder(String name, OWNER owner, BiFunction<Item.Properties, Integer, DIE> diceFactory)
	{
		return new Builder<>(name, owner, diceFactory);
	}

	public static <OWNER extends CoreRegistrate<OWNER> & ItemHolder<OWNER>> Builder<OWNER, DiceItem> builder(String name, OWNER owner)
	{
		return builder(name, owner, DiceItem::new);
	}

	public static List<DiceType<?, ?>> getDiceTypes()
	{
		return diceTypes;
	}

	public static final class Builder<OWNER extends CoreRegistrate<OWNER> & ItemHolder<OWNER>, DIE extends DiceItem>
	{
		private final String name;
		private final OWNER owner;
		private final TagKey<Item> tag;
		private final Int2ObjectMap<String> dieNames = new Int2ObjectOpenHashMap<>();
		private final BiFunction<Item.Properties, Integer, DIE> diceFactory;

		private Type type = Type.REGULAR;
		private IntSupplier diceQuality = () -> 0;
		private BiFunction<ItemStack, Style, Style> styleModifier = (stack, style) -> style;
		private RollCallback rollCallback = (player, hand, stack, min, sides, rolls, dieQuality) -> rolls;
		private boolean usesFoil = false;

		private Builder(String name, OWNER owner, BiFunction<Item.Properties, Integer, DIE> diceFactory)
		{
			this.name = name;
			this.owner = owner;
			this.diceFactory = diceFactory;

			tag = ItemTags.tag(owner.getModId(), "dice/" + name);
			owner.addDataGenerator(ProviderType.ITEM_TAGS, provider -> provider.addTag(FTTags.Items.DICE).addTag(tag));
		}

		public Builder<OWNER, DIE> withType(Type type)
		{
			this.type = type;
			return this;
		}

		public Builder<OWNER, DIE> withDiceQuality(IntSupplier diceQuality)
		{
			this.diceQuality = diceQuality;
			return this;
		}

		public Builder<OWNER, DIE> withStyle(BiFunction<ItemStack, Style, Style> nameStyle)
		{
			this.styleModifier = nameStyle;
			return this;
		}

		public Builder<OWNER, DIE> onRoll(RollCallback rollCallback)
		{
			this.rollCallback = rollCallback;
			return this;
		}

		public ItemBuilder<OWNER, DIE, Builder<OWNER, DIE>> withDie(int sides)
		{
			return owner
					.object(generateDieName(sides))
					.item(this, properties -> diceFactory.apply(properties, sides))
					.tag(tag)
					.lang("%d-Sided %s Die".formatted(sides, RegistrateLangProvider.toEnglishName(name)))
					.stacksTo(8)
					.model((ctx, provider) -> provider.generated(ctx, new ResourceLocation(Mods.FANTASY_DICE, "item/die/%s/%d_sided".formatted(name, sides))))
			;
		}

		public Builder<OWNER, DIE> withSimpleDie(int sides)
		{
			return withDie(sides).build();
		}

		public Builder<OWNER, DIE> usesFoil()
		{
			usesFoil = true;
			return this;
		}

		public DiceType<OWNER, DIE> build()
		{
			return new DiceType<>(this);
		}

		private String generateDieName(int sides)
		{
			return dieNames.computeIfAbsent(sides, $ -> $ + "_sided_" + name + "_die");
		}
	}

	@FunctionalInterface
	public interface RollCallback
	{
		int onRoll(Player player, InteractionHand hand, ItemStack stack, int min, int sides, int roll, int dieQuality);
	}

	public enum Type
	{
		REGULAR(Mods.FANTASY_DICE, "regular"),
		COSMETIC(Mods.FANTASY_DICE, "cosmetic"),
		SPECIALITY(Mods.FANTASY_DICE, "specialty");

		public static final Type[] VALUES = values();

		private final String translationKey;

		Type(String modId, String name)
		{
			this(String.format("%s.die.type.%s.name", modId, name));
		}

		Type(String translationKey)
		{
			this.translationKey = translationKey;
		}

		public String getTranslationKey()
		{
			return translationKey;
		}

		public MutableComponent getComponent(ItemStack stack, DiceType<?, ?> diceType)
		{
			var diceQuality = diceType.getDiceQuality();
			return Component.translatable(translationKey)
					.append(diceQuality > 0 ? " (+%d)".formatted(diceQuality) : " (%d)".formatted(diceQuality))
					.withStyle(style -> diceType
							.withStyle(stack, style)
							.withItalic(true)
					);
		}
	}
}
