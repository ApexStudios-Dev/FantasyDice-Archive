package xyz.apex.forge.fantasydice.init;

import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;

import xyz.apex.forge.fantasydice.item.DiceItem;
import xyz.apex.forge.fantasydice.item.DyeableDiceItem;
import xyz.apex.forge.fantasydice.util.DiceHelper;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.repack.com.tterrag.registrate.providers.DataGenContext;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public final class FTDiceTypes
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	// region: Wooden
	public static final DiceType<FTRegistry, DiceItem> DICE_WOODEN = DiceType
			.builder("wooden", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF8A5A27)))
				.withRollAddition(-3)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, ItemTags.WOODEN_BUTTONS))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, ItemTags.WOODEN_BUTTONS))
				.build()
			.build();
	// endregion

	// region: Stone
	public static final DiceType<FTRegistry, DiceItem> DICE_STONE = DiceType
			.builder("stone", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.GRAY))
				.withRollAddition(-2)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Blocks.STONE_BUTTON))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Blocks.STONE_BUTTON))
				.build()
			.build();
	// endregion

	// region: Bone
	public static final DiceType<FTRegistry, DiceItem> DICE_BONE = DiceType
			.builder("bone", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.WHITE))
				.withRollAddition(-1)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.BONES))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.BONES))
				.build()
			.build();
	// endregion

	// region: Paper
	public static final DiceType<FTRegistry, DyeableDiceItem> DICE_PAPER = DiceType
			.builder("paper", REGISTRY, DyeableDiceItem::new)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.WHITE))

				.withDie(6)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Items.PAPER))
				.build()

				.withDie(20)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Items.PAPER))
				.build()
			.build();
	// endregion

	// region: Iron
	public static final DiceType<FTRegistry, DiceItem> DICE_IRON = DiceType
			.builder("iron", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.GRAY))

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()
			.build();
	// endregion

	// region: Golden
	public static final DiceType<FTRegistry, DiceItem> DICE_GOLD = DiceType
			.builder("golden", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.YELLOW))
				.withRollAddition(1)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()
			.build();
	// endregion

	// region: Diamond
	public static final DiceType<FTRegistry, DiceItem> DICE_DIAMOND = DiceType
			.builder("diamond", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.AQUA))
				.withRollAddition(2)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()
			.build();
	// endregion

	// region: Emerald
	public static final DiceType<FTRegistry, DiceItem> DICE_EMERALD = DiceType
			.builder("emerald", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.GREEN))
				.withRollAddition(3)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()
			.build();
	// endregion

	// region: Fantasy
	public static final DiceType<FTRegistry, DiceItem> DICE_FANTASY = DiceType
			.builder("fantasy", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFF39F9F)))
				.onRoll((player, hand, stack, min, sides, rolls) -> {
					Random rng = player.getRandom();

					int newMin;
					int newMax;

					if(DiceHelper.isLuckyRoller(player))
					{
						newMin = sides / 2;
						newMax = sides;
					}
					else
					{
						newMin = min;
						newMax = sides / 2;
					}

					Arrays.setAll(rolls, i -> DiceHelper.roll(rng, newMin, newMax));
					return rolls;
				})

				.withDie(6)
					.lang("Fantasy's Lucky 6-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Fantasy's Lucky 6-Sided Die")
				.build()

				.withDie(20)
					.lang("Fantasy's Lucky 20-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Fantasy's Lucky 20-Sided Die")
				.build()
			.build();
	// endregion

	// region: Tobi
	public static final DiceType<FTRegistry, DiceItem> DICE_TOBI = DiceType
			.builder("tobi", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.DARK_PURPLE))
				.onRoll((player, hand, stack, min, sides, rolls) -> {
					Random rng = player.getRandom();

					for(int i = 0; i < rolls.length; i++)
					{
						rolls[i] = IntStream.range(0, 3).map($ -> DiceHelper.roll(rng, min, sides)).max().orElse(rolls[i]);
					}

					return rolls;
				})

				.withDie(6)
					.lang("Tobi's Thrice 6-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Tobi's Thrice 6-Sided Die")
					.stacksTo(1)
				.build()

				.withDie(20)
					.lang("Tobi's Thrice 20-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Tobi's Thrice 20-Sided Die")
					.stacksTo(1)
				.build()
			.build();
	// endregion

	// region: Apex
	public static final DiceType<FTRegistry, DiceItem> DICE_APEX = DiceType
			.builder("apex", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.DARK_PURPLE))
				.onRoll((player, hand, stack, min, sides, rolls) -> {
					Arrays.setAll(rolls, i -> rolls[i] * -1);
					return rolls;
				})

				.withDie(6)
					.lang("Apex's NULL 6-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Apex's NULL 6-Sided Die")
				.build()

				.withDie(20)
					.lang("Apex's NULL 20-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Apex's NULL 20-Sided Die")
				.build()
			.build();
	// endregion

	// region: Symacon
	public static final DiceType<FTRegistry, DiceItem> DICE_SYMACON = DiceType
			.builder("symacon", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFFF681F)))
				.onRoll((player, hand, stack, min, sides, rolls) -> {
					boolean half = player.getRandom().nextBoolean();
					return IntStream.of(rolls).map(i -> half ? i / 2 : i * 2).toArray();
				})

				.withDie(6)
					.lang("Symacon's Gambling 6-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Symacon's Gambling 6-Sided Die")
				.build()

				.withDie(20)
					.lang("Symacon's Gambling 20-Sided Die")
					.lang(RegistrateLangExtProvider.EN_GB, "Symacon's Gambling 20-Sided Die")
				.build()
			.build();
	// endregion

	static void bootstrap()
	{
	}

	// region: Styles
	private static Style colorOrDyed(ItemStack stack, Style style, Color color)
	{
		Item item = stack.getItem();

		if(!stack.isEmpty() && item instanceof IDyeableArmorItem)
		{
			int dyedColor = ((IDyeableArmorItem) item).getColor(stack);
			return style.withColor(Color.fromRgb(dyedColor));
		}

		return style.withColor(color);
	}

	private static Style colorOrDyed(ItemStack stack, Style style, TextFormatting formatting)
	{
		Item item = stack.getItem();

		if(!stack.isEmpty() && item instanceof IDyeableArmorItem)
		{
			int dyedColor = ((IDyeableArmorItem) item).getColor(stack);
			return style.withColor(Color.fromRgb(dyedColor));
		}

		return style.withColor(formatting);
	}
	// endregion

	// region: Recipes
	private static <D extends DiceItem> void recipeSixSided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, ITag.INamedTag<Item> ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern("II")
		                   .pattern("II")
		                   .group("dice/6_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeTwentySided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, ITag.INamedTag<Item> ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern(" I ")
		                   .pattern("III")
		                   .pattern(" I ")
		                   .group("dice/20_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeSixSided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, IItemProvider ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern("II")
		                   .pattern("II")
		                   .group("dice/6_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeTwentySided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, IItemProvider ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern(" I ")
		                   .pattern("III")
		                   .pattern(" I ")
		                   .group("dice/20_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient))
		                   .save(provider, ctx.getId());
	}
	// endregion
}
