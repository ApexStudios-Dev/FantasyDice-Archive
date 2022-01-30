package xyz.apex.forge.fantasydice.init;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.apache.commons.lang3.Validate;

import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Style;

import xyz.apex.forge.fantasydice.item.DiceItem;
import xyz.apex.forge.utility.registrator.AbstractRegistrator;
import xyz.apex.forge.utility.registrator.builder.ItemBuilder;
import xyz.apex.forge.utility.registrator.entry.ItemEntry;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.java.utility.function.TriFunction;
import xyz.apex.java.utility.nullness.NonnullBiFunction;
import xyz.apex.java.utility.nullness.NonnullType;
import xyz.apex.java.utility.nullness.NullableType;
import xyz.apex.repack.com.tterrag.registrate.providers.DataGenContext;
import xyz.apex.repack.com.tterrag.registrate.providers.ProviderType;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateLangProvider;
import xyz.apex.repack.com.tterrag.registrate.util.entry.RegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

public final class DiceType<OWNER extends AbstractRegistrator<OWNER>, DIE extends DiceItem>
{
	private static final List<DiceType<?, ?>> diceTypes = Lists.newArrayList();

	private final String name;
	private final OWNER owner;
	private final Int2ObjectMap<ItemEntry<DIE>> diceItems = new Int2ObjectOpenHashMap<>();
	private final ITag.INamedTag<Item> tag;
	private final NonnullBiFunction<ItemStack, Style, Style> styleModifier;
	private final RollCallback rollCallback;
	private final boolean usesFoil;
	private final int rollAddition;

	private DiceType(Builder<OWNER, DIE> builder)
	{
		name = Validate.notBlank(builder.name);
		owner = builder.owner;
		tag = builder.tag;
		styleModifier = builder.styleModifier;
		rollCallback = builder.rollCallback;
		usesFoil = builder.usesFoil;
		rollAddition = builder.rollAddition;

		for(Int2ObjectMap.Entry<String> entry : builder.dieNames.int2ObjectEntrySet())
		{
			int sides = entry.getIntKey();
			String dieName = entry.getValue();

			RegistryEntry<DIE> registryEntry = owner.get(dieName, Item.class);
			ItemEntry<DIE> itemEntry = ItemEntry.cast(registryEntry);

			diceItems.put(sides, itemEntry);

			if(builder.recipeModifier != null)
			{
				owner.addDataGenerator(ProviderType.RECIPE, provider -> {
					DataGenContext<Item, DIE> ctx = new DataGenContext<>(itemEntry, dieName, itemEntry.getId());
					ShapedRecipeBuilder recipe = ShapedRecipeBuilder.shaped(itemEntry, 8).group("dice/" + name);
					recipe = builder.recipeModifier.apply(sides, ctx, recipe);

					if(recipe != null)
						recipe.save(provider);
				});
			}
		}

		diceTypes.add(this);

		owner.addRegisterCallback(Item.class, () -> diceItems
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

	public ITag.INamedTag<Item> getTag()
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

	public ObjectCollection<ItemEntry<DIE>> getItems()
	{
		return diceItems.values();
	}

	public int getRollAddition()
	{
		return rollAddition;
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

	public int[] onRoll(PlayerEntity player, Hand hand, ItemStack stack, int min, int sides, int[] rolls)
	{
		return rollCallback.onRoll(player, hand, stack, min, sides, rolls);
	}

	public static <OWNER extends AbstractRegistrator<OWNER>, DIE extends DiceItem> Builder<OWNER, DIE> builder(String name, OWNER owner, NonnullBiFunction<Item.Properties, Integer, DIE> diceFactory)
	{
		return new Builder<>(name, owner, diceFactory);
	}

	public static <OWNER extends AbstractRegistrator<OWNER>> Builder<OWNER, DiceItem> builder(String name, OWNER owner)
	{
		return builder(name, owner, DiceItem::new);
	}

	public static List<DiceType<?, ?>> getDiceTypes()
	{
		return diceTypes;
	}

	public static final class Builder<OWNER extends AbstractRegistrator<OWNER>, DIE extends DiceItem>
	{
		private final String name;
		private final OWNER owner;
		private final ITag.INamedTag<Item> tag;
		private final Int2ObjectMap<String> dieNames = new Int2ObjectOpenHashMap<>();
		private final NonnullBiFunction<Item.Properties, Integer, DIE> diceFactory;

		private NonnullBiFunction<ItemStack, Style, Style> styleModifier = (stack, style) -> style;
		@Nullable private TriFunction<@NonnullType Integer, @NonnullType DataGenContext<Item, DIE>, @NonnullType ShapedRecipeBuilder, @NullableType ShapedRecipeBuilder> recipeModifier = null;
		private RollCallback rollCallback = (player, hand, stack, min, sides, rolls) -> rolls;
		private boolean usesFoil = false;
		private int rollAddition = 0;

		private Builder(String name, OWNER owner, NonnullBiFunction<Item.Properties, Integer, DIE> diceFactory)
		{
			this.name = name;
			this.owner = owner;
			this.diceFactory = diceFactory;

			tag = owner.itemTagModded("dice/" + name);
			owner.addDataGenerator(ProviderType.ITEM_TAGS, provider -> provider.tag(FTTags.Items.DICE).addTag(tag));
		}

		public Builder<OWNER, DIE> withRollAddition(int rollAddition)
		{
			this.rollAddition = rollAddition;
			return this;
		}

		public Builder<OWNER, DIE> withStyle(NonnullBiFunction<ItemStack, Style, Style> nameStyle)
		{
			this.styleModifier = nameStyle;
			return this;
		}

		public Builder<OWNER, DIE> withRecipe(TriFunction<@NonnullType Integer, @NonnullType DataGenContext<Item, DIE>, @NonnullType ShapedRecipeBuilder, @NullableType ShapedRecipeBuilder> recipeModifier)
		{
			this.recipeModifier = recipeModifier;
			return this;
		}

		public Builder<OWNER, DIE> onRoll(RollCallback rollCallback)
		{
			this.rollCallback = rollCallback;
			return this;
		}

		public ItemBuilder<OWNER, DIE, Builder<OWNER, DIE>> withDie(int sides)
		{
			String dieName = generateDieName(sides);

			String englishName = RegistrateLangProvider.toEnglishName(name);
			String fullName = sides + "-Sided " + englishName + " Die";

			return owner
					.item(dieName, this, properties -> diceFactory.apply(properties, sides))
					.tag(tag)
					.lang(fullName)
					.lang(RegistrateLangExtProvider.EN_GB, fullName)
					.stacksTo(8)
					.model((ctx, provider) -> provider.generated(ctx, owner.id("item/die/" + name + "/" + sides + "_sided")))
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
		int[] onRoll(PlayerEntity player, Hand hand, ItemStack stack, int min, int sides, int[] rolls);
	}
}
