package xyz.apex.minecraft.fantasydice.common;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.registry.Registrar;
import xyz.apex.minecraft.apexcore.common.lib.registry.builder.ItemBuilder;
import xyz.apex.minecraft.apexcore.common.lib.registry.entry.ItemEntry;
import xyz.apex.minecraft.apexcore.common.lib.resgen.ProviderTypes;
import xyz.apex.minecraft.apexcore.common.lib.resgen.lang.LanguageProvider;
import xyz.apex.minecraft.fantasydice.common.item.CoinItem;

@ApiStatus.NonExtendable
public interface Coins
{
    String TXT_FLIPPED = "misc.%s.coin_flipped";
    String TXT_FLIPPED_COUNT = "misc.%s.coin_flipped.count";

    ItemEntry<CoinItem> IRON = coin("iron", 0xFFE0E0E0)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.IRON_NUGGET)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.IRON_NUGGET))
                    .save(provider::add, entry.getRegistryName())
            )
    .register();

    ItemEntry<CoinItem> GOLDEN = coin("golden", 0xFFE8C037)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.GOLD_NUGGET)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.GOLD_NUGGET))
                    .save(provider::add, entry.getRegistryName())
            )
    .register();

    ItemEntry<CoinItem> DIAMOND = coin("diamond", 0x55FFFF)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.DIAMOND)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.DIAMOND))
                    .save(provider::add, entry.getRegistryName())
            )
    .register();

    ItemEntry<CoinItem> EMERALD = coin("emerald", 0xFF1F8B20)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.EMERALD)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.EMERALD))
                    .save(provider::add, entry.getRegistryName())
            )
    .register();

    ItemEntry<CoinItem> NETHERITE = coin("netherite", 0xFF423030)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.NETHERITE_INGOT)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.NETHERITE_INGOT))
                    .save(provider::add, entry.getRegistryName())
            )
    .register();

    ItemEntry<CoinItem> AMETHYST = coin("amethyst", 0xFFC991EB)
            .recipe((provider, lookup, entry) -> ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.MISC, entry)
                    .requires(Items.AMETHYST_SHARD)
                    .group("coin")
                    .unlockedBy("has_ingredient", provider.has(Items.AMETHYST_SHARD))
                    .save(provider::add, entry.getRegistryName())
            )
            .register();

    @ApiStatus.Internal
    static void bootstrap()
    {
        ProviderTypes.LANGUAGES.addListener(FantasyDice.ID, (provider, lookup) -> provider
                .enUS()
                    .add(TXT_FLIPPED, "%s Flipped %s")
                    .add(TXT_FLIPPED_COUNT, "%s Heads and %s Tails")
                .end()
        );
    }

    private static ItemBuilder<Registrar, CoinItem, Registrar> coin(String type, int color)
    {
        return FantasyDice.REGISTRAR
                .object("coin/%s".formatted(type))
                .item(properties -> new CoinItem(TextColor.fromRgb(color), properties))
                .stacksTo(8)
                .lang("en_us", "%s Coin".formatted(LanguageProvider.toEnglishName(type)))
        ;
    }
}
