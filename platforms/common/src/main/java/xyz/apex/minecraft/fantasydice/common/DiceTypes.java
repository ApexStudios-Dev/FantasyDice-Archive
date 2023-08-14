package xyz.apex.minecraft.fantasydice.common;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.resgen.ProviderTypes;
import xyz.apex.minecraft.fantasydice.common.item.DyeableDiceItem;
import xyz.apex.minecraft.fantasydice.common.util.DiceType;

import java.util.stream.IntStream;

@ApiStatus.NonExtendable
public interface DiceTypes
{
    String TXT_FLIPPED = "misc.%s.dice_roll";
    String TXT_FLIPPED_COUNT = "misc.%s.dice_roll.count";

    // region: Wooden
    DiceType WOODEN = DiceType.builder("wooden", 0xFF8A5A27)
            .withRecipe(RecipeCategory.TOOLS, ItemTags.PLANKS)
            .with(4, 6, 8, 10, 12, 20)
    .build();
    // endregion

    // region: Stone
    DiceType STONE = DiceType.builder("stone", 0xAAAAAA)
            .withRecipe(RecipeCategory.TOOLS, ItemTags.STONE_CRAFTING_MATERIALS)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Bone
    DiceType BONE = DiceType.builder("bone", 0xFFEDDABC)
            .withRecipe(RecipeCategory.TOOLS, Items.BONE)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Iron
    DiceType IRON = DiceType.builder("iron", 0xFFE0E0E0)
            .withRecipe(RecipeCategory.TOOLS, Items.IRON_INGOT)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Golden
    DiceType GOLDEN = DiceType.builder("golden", 0xFFE8C037)
            .withRecipe(RecipeCategory.TOOLS, Items.GOLD_INGOT)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Diamond
    DiceType DIAMOND = DiceType.builder("diamond", 0x55FFFF)
            .withRecipe(RecipeCategory.TOOLS, Items.DIAMOND)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Emerald
    DiceType EMERALD = DiceType.builder("emerald", 0xFF1F8B20)
            .withRecipe(RecipeCategory.TOOLS, Items.EMERALD)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Netherite
    DiceType NETHERITE = DiceType.builder("netherite", 0xFF423030)
            .withRecipe(RecipeCategory.TOOLS, Items.NETHERITE_INGOT)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Copper
    DiceType COPPER = DiceType.builder("copper", 0xFFD4784D)
            .withRecipe(RecipeCategory.TOOLS, Items.COPPER_INGOT)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Ender
    DiceType ENDER = DiceType.builder("ender", 0xFF0F5959)
            .withRecipe(RecipeCategory.TOOLS, Items.ENDER_PEARL)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Frozen
    DiceType FROZEN = DiceType.builder("frozen", 0xFFBADED8)
            .withRecipe(RecipeCategory.TOOLS, Items.ICE)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Slime
    DiceType SLIME = DiceType.builder("slime", 0xFF8ACC83)
            .withRecipe(RecipeCategory.TOOLS, Items.SLIME_BALL)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Redstone
    DiceType REDSTONE = DiceType.builder("redstone", 0xFF931515)
            .withRecipe(RecipeCategory.TOOLS, Items.REDSTONE)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Paper
    DiceType PAPER = DiceType.builder("paper", 0xFFFFFF, DyeableDiceItem::new)
            .withRecipe(RecipeCategory.TOOLS, Items.PAPER)
            .with((builder, side) -> builder.colorHandler(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? ((DyeableLeatherItem) stack.getItem()).getColor(stack) : -1), 4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Amethyst
    DiceType AMETHYST = DiceType.builder("amethyst", 0xFFC991EB)
            .withRecipe(RecipeCategory.TOOLS, Items.AMETHYST_SHARD)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Chocolate
    DiceType CHOCOLATE = DiceType.builder("chocolate", 0xFF673B27)
            .withRecipe(RecipeCategory.TOOLS, Items.COCOA_BEANS)
            .with(4, 6, 8, 10, 12, 20)
            .build();
    // endregion

    // region: Fantasy
    DiceType FANTASY = DiceType.builder("fantasy", 0xFFF39F9F)
            .with((builder, side) -> builder.lang("en_us", "Fantasy's Lucky %d-Sided Die".formatted(side)), 6, 20)
            .withRollModifier((roller, dice, sides, originalRoll) -> {
                var luck = roller.getLuck();

                if(luck > 0F)
                    return roller.getRandom().nextIntBetweenInclusive(sides / 2, sides);
                else if(luck < 0F)
                    return roller.getRandom().nextIntBetweenInclusive(0, sides / 2);

                return originalRoll;
            })
            .build();
    // endregion

    // region: Tobi
    DiceType TOBI = DiceType.builder("tobi", 0xFF5B20A2)
            .with((builder, side) -> builder.lang("en_us", "Tobi's Thrice %d-Sided Die".formatted(side)).stacksTo(1), 6, 20)
            .withRollModifier((roller, dice, sides, originalRoll) -> IntStream.range(0, 3).map(i -> roller.getRandom().nextIntBetweenInclusive(0, sides)).max().orElse(originalRoll))
            .build();
    // endregion

    // region: Apex
    DiceType APEX = DiceType.builder("apex", 0xAA00AA)
            .with((builder, side) -> builder.lang("en_us", "Apex's NULL %d-Sided Die".formatted(side)), 6, 20)
            .withRollModifier((roller, dice, sides, originalRoll) -> originalRoll * -1)
            .build();
    // endregion

    // region: Symacon
    DiceType SYMACON = DiceType.builder("symacon", 0xFFFF681F)
            .with((builder, side) -> builder.lang("en_us", "Symacons's Gambling %d-Sided Die".formatted(side)), 6, 20)
            .withRollModifier((roller, dice, sides, originalRoll) -> roller.getRandom().nextBoolean() ? originalRoll / 2 : originalRoll * 2)
            .build();
    // endregion

    @ApiStatus.Internal
    static void bootstrap()
    {
        ProviderTypes.LANGUAGES.addListener(FantasyDice.ID, (provider, lookup) -> provider
                .enUS()
                    .add(TXT_FLIPPED, "%s rolls %s")
                    .add(TXT_FLIPPED_COUNT, "%s (%sd%s)")
                .end()
        );
    }
}
