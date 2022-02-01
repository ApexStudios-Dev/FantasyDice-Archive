package xyz.apex.forge.fantasydice.init;

import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import xyz.apex.forge.fantasydice.FantasyDice;
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextColor.fromRgb(0xFF8A5A27)))
				.withDiceQuality(FantasyDice.CONFIG.diceWoodenQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.GRAY))
				.withDiceQuality(FantasyDice.CONFIG.diceStoneQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.WHITE))
				.withDiceQuality(FantasyDice.CONFIG.diceBoneQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.WHITE))
				.withDiceQuality(FantasyDice.CONFIG.dicePaperQuality::get)

				.withDie(6)
					.color(() -> () -> (stack, tintIndex) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Items.PAPER))
				.build()

				.withDie(20)
					.color(() -> () -> (stack, tintIndex) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Items.PAPER))
				.build()
			.build();
	// endregion

	// region: Iron
	public static final DiceType<FTRegistry, DiceItem> DICE_IRON = DiceType
			.builder("iron", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.GRAY))
				.withDiceQuality(FantasyDice.CONFIG.diceIronQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.YELLOW))
				.withDiceQuality(FantasyDice.CONFIG.diceGoldenQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.AQUA))
				.withDiceQuality(FantasyDice.CONFIG.diceDiamondQuality::get)

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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.GREEN))
				.withDiceQuality(FantasyDice.CONFIG.diceEmeraldQuality::get)

				.withDie(6)
					.recipe((ctx, provider) -> recipeSixSided(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> recipeTwentySided(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()
			.build();
	// endregion

	// region: Netherite
	/*public static final DiceType<FTRegistry, DiceItem> DICE_NETHERITE = DiceType
			.builder("netherite", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF5A575A)))
				.withDiceQuality(FantasyDice.CONFIG.diceNetheriteQuality::get)

				.withDie(6)
					.recipe((ctx, provider) -> smithing(ctx, provider, DICE_DIAMOND.getItem(6)))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> smithing(ctx, provider, DICE_DIAMOND.getItem(20)))
				.build()
			.build();*/
	// endregion

	// region: Fantasy
	public static final DiceType<FTRegistry, DiceItem> DICE_FANTASY = DiceType
			.builder("fantasy", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextColor.fromRgb(0xFFF39F9F)))
				.onRoll((player, hand, stack, min, sides, rolls, dieQuality) -> {
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

					Arrays.setAll(rolls, i -> DiceHelper.roll(rng, newMin, newMax, dieQuality));
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.DARK_PURPLE))
				.onRoll((player, hand, stack, min, sides, rolls, dieQuality) -> {
					Random rng = player.getRandom();

					for(int i = 0; i < rolls.length; i++)
					{
						rolls[i] = IntStream.range(0, 3).map($ -> DiceHelper.roll(rng, min, sides, dieQuality)).max().orElse(rolls[i]);
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, ChatFormatting.DARK_PURPLE))
				.onRoll((player, hand, stack, min, sides, rolls, dieQuality) -> {
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextColor.fromRgb(0xFFFF681F)))
				.onRoll((player, hand, stack, min, sides, rolls, dieQuality) -> {
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
	private static Style colorOrDyed(ItemStack stack, Style style, TextColor color)
	{
		var item = stack.getItem();

		if(!stack.isEmpty() && item instanceof DyeableLeatherItem dyeable)
		{
			var dyedColor = dyeable.getColor(stack);
			return style.withColor(TextColor.fromRgb(dyedColor));
		}

		return style.withColor(color);
	}

	private static Style colorOrDyed(ItemStack stack, Style style, ChatFormatting formatting)
	{
		var item = stack.getItem();

		if(!stack.isEmpty() && item instanceof DyeableLeatherItem dyeable)
		{
			var dyedColor = dyeable.getColor(stack);
			return style.withColor(TextColor.fromRgb(dyedColor));
		}

		return style.withColor(formatting);
	}
	// endregion

	// region: Recipes
	// TODO: This should probaly be merged into ApexCore or Registrator
	private static <I extends Item> void smithing(DataGenContext<Item, I> ctx, RegistrateRecipeProvider provider, ItemLike input)
	{
		UpgradeRecipeBuilder.smithing(Ingredient.of(input), Ingredient.of(Tags.Items.INGOTS_NETHERITE), ctx.getEntry())
		                    .unlocks("has_netherite_ingot", RegistrateRecipeProvider.has(Tags.Items.INGOTS_NETHERITE))
		                    .save(provider, ctx.getId() + "_smithing");
	}

	private static <D extends DiceItem> void recipeSixSided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, Tag.Named<Item> ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern("II")
		                   .pattern("II")
		                   .group("dice/6_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.has(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeTwentySided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, Tag.Named<Item> ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern(" I ")
		                   .pattern("III")
		                   .pattern(" I ")
		                   .group("dice/20_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.has(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeSixSided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, ItemLike ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern("II")
		                   .pattern("II")
		                   .group("dice/6_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.has(ingredient))
		                   .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void recipeTwentySided(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, ItemLike ingredient)
	{
		ShapedRecipeBuilder.shaped(ctx::get, 8)
		                   .define('I', ingredient)
		                   .pattern(" I ")
		                   .pattern("III")
		                   .pattern(" I ")
		                   .group("dice/20_sided")
		                   .unlockedBy("has_item", RegistrateRecipeProvider.has(ingredient))
		                   .save(provider, ctx.getId());
	}
	// endregion
}
