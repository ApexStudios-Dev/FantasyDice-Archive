package xyz.apex.minecraft.fantasydice.common.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.registry.Registrar;
import xyz.apex.minecraft.apexcore.common.lib.registry.builder.ItemBuilder;
import xyz.apex.minecraft.apexcore.common.lib.registry.entry.ItemEntry;
import xyz.apex.minecraft.apexcore.common.lib.resgen.RecipeProvider;
import xyz.apex.minecraft.apexcore.common.lib.resgen.lang.LanguageProvider;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.item.DiceItem;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class DiceType
{
    private final String name;
    private final TextColor color;
    private final Int2ObjectMap<ItemEntry<DiceItem>> items;
    private final DiceRollModifier rollModifier;

    DiceType(String name, TextColor color, Int2ObjectMap<Consumer<ItemBuilder<Registrar, DiceItem, Registrar>>> builders, @Nullable BiFunction<RecipeProvider, ItemEntry<DiceItem>, SingleItemRecipeBuilder> recipeFactory, DiceRollModifier rollModifier, TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory)
    {
        this.name = name;
        this.color = color;
        this.rollModifier = rollModifier;

        var items = new Int2ObjectOpenHashMap<ItemEntry<DiceItem>>();

        for(var entry : builders.int2ObjectEntrySet())
        {
            var sides = entry.getIntKey();
            var builder = dice(this, sides, recipeFactory, itemFactory);
            entry.getValue().accept(builder);
            Validate.isTrue(items.put(sides, builder.register()) == null);
        }

        this.items = Int2ObjectMaps.unmodifiable(items);
    }

    public String getName()
    {
        return name;
    }

    public TextColor getColor()
    {
        return color;
    }

    @ApiStatus.Internal
    public int modifyRoll(Player roller, ItemStack dice, int sides, int originalRoll)
    {
        return rollModifier.modify(roller, dice, sides, originalRoll);
    }

    public Optional<ItemEntry<DiceItem>> find(int sides)
    {
        return Optional.ofNullable(items.get(sides));
    }

    public ItemEntry<DiceItem> get(int sides)
    {
        return Objects.requireNonNull(items.get(sides));
    }

    public static DiceBuilder builder(String name, TextColor color, TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory)
    {
        return new DiceBuilder(name, color, itemFactory);
    }

    public static DiceBuilder builder(String name, int color, TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory)
    {
        return builder(name, TextColor.fromRgb(color), itemFactory);
    }

    public static DiceBuilder builder(String name, TextColor color)
    {
        return builder(name, color, DiceItem::new);
    }

    public static DiceBuilder builder(String name, int color)
    {
        return builder(name, color, DiceItem::new);
    }

    private static ItemBuilder<Registrar, DiceItem, Registrar> dice(DiceType diceType, int sides, @Nullable BiFunction<RecipeProvider, ItemEntry<DiceItem>, SingleItemRecipeBuilder> recipeFactory, TriFunction<DiceType, Integer, Item.Properties, DiceItem> itemFactory)
    {
        return FantasyDice.REGISTRAR
                .object("dice/%s/%d_sided".formatted(diceType.name, sides))
                .item(properties -> itemFactory.apply(diceType, sides, properties))
                .lang("en_us", "%d-Sided %s Die".formatted(sides, LanguageProvider.toEnglishName(diceType.name)))
                .stacksTo(8)
                .recipe((provider, lookup, entry) -> {
                    if(recipeFactory == null)
                        return;

                    var recipe = recipeFactory.apply(provider, entry);

                    if(recipe != null)
                        recipe.save(provider::add, entry.getRegistryName());
                })
        ;
    }
}
