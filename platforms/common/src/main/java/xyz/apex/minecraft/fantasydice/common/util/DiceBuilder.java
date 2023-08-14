package xyz.apex.minecraft.fantasydice.common.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.function.TriFunction;
import xyz.apex.minecraft.apexcore.common.lib.registry.Registrar;
import xyz.apex.minecraft.apexcore.common.lib.registry.builder.ItemBuilder;
import xyz.apex.minecraft.apexcore.common.lib.registry.entry.ItemEntry;
import xyz.apex.minecraft.apexcore.common.lib.resgen.RecipeProvider;
import xyz.apex.minecraft.fantasydice.common.item.DiceItem;
import xyz.apex.minecraft.fantasydice.common.recipe.DiceStationRecipe;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.IntStream;

public final class DiceBuilder
{
    private final String name;
    private final TextColor color;
    private final Int2ObjectMap<Consumer<ItemBuilder<Registrar, DiceItem, Registrar>>> diceTypes = new Int2ObjectOpenHashMap<>();
    @Nullable private BiFunction<RecipeProvider, ItemEntry<DiceItem>, SingleItemRecipeBuilder> recipeFactory = null;
    private DiceRollModifier rollModifier = (roller, dice, sides, originalRoll) -> originalRoll;
    private final TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory;

    DiceBuilder(String name, TextColor color, TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory)
    {
        this.name = name;
        this.color = color;
        this.itemFactory = itemFactory;
    }

    public DiceBuilder with(int sides, Consumer<ItemBuilder<Registrar, DiceItem, Registrar>> builder)
    {
        if(diceTypes.put(sides, builder) != null)
            throw new IllegalStateException("DiceItem already exists with side count; %d".formatted(sides));

        return this;
    }

    public DiceBuilder with(int sides)
    {
        return with(sides, $ -> { });
    }

    public DiceBuilder with(ObjIntConsumer<ItemBuilder<Registrar, DiceItem, Registrar>> builder, int... sides)
    {
        IntStream.of(sides).forEach(side -> with(side, bldr -> builder.accept(bldr, side)));
        return this;
    }

    public DiceBuilder with(int... sides)
    {
        return with((side, builder) -> { }, sides);
    }

    public DiceBuilder withRecipe(RecipeCategory category, TagKey<Item> ingredient, int count)
    {
        recipeFactory = (provider, entry) -> DiceStationRecipe.builder(category, Ingredient.of(ingredient), entry, count)
                .unlockedBy("has_ingredient", provider.has(ingredient))
                .group(name)
        ;
        return this;
    }

    public DiceBuilder withRecipe(RecipeCategory category, TagKey<Item> ingredient)
    {
        return withRecipe(category, ingredient, 1);
    }

    public DiceBuilder withRecipe(RecipeCategory category, ItemLike ingredient, int count)
    {
        recipeFactory = (provider, entry) -> DiceStationRecipe.builder(category, Ingredient.of(ingredient), entry, count)
                .unlockedBy("has_ingredient", provider.has(ingredient))
                .group(name)
        ;
        return this;
    }

    public DiceBuilder withRecipe(RecipeCategory category, ItemLike ingredient)
    {
        return withRecipe(category, ingredient, 1);
    }

    public DiceBuilder withRollModifier(DiceRollModifier rollModifier)
    {
        this.rollModifier = rollModifier;
        return this;
    }

    public DiceType build()
    {
        return new DiceType(name, color, Int2ObjectMaps.unmodifiable(diceTypes), recipeFactory, rollModifier, itemFactory);
    }
}
