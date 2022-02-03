package xyz.apex.forge.fantasydice.init;

import net.minecraft.block.Blocks;
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

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.item.DiceItem;
import xyz.apex.forge.fantasydice.item.DyeableDiceItem;
import xyz.apex.forge.fantasydice.util.DiceHelper;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.repack.com.tterrag.registrate.providers.DataGenContext;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;
import xyz.apex.repack.com.tterrag.registrate.util.DataIngredient;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public final class FTDiceTypes
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	// region: Regular
	// region: Wooden
	public static final DiceType<FTRegistry, DiceItem> DICE_WOODEN = DiceType
			.builder("wooden", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF8A5A27)))
				.withDiceQuality(FantasyDice.CONFIG.diceWoodenQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, ItemTags.PLANKS))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, ItemTags.PLANKS))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, ItemTags.PLANKS))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, ItemTags.PLANKS))
				.build()
			.build();
	// endregion

	// region: Stone
	public static final DiceType<FTRegistry, DiceItem> DICE_STONE = DiceType
			.builder("stone", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.GRAY))
				.withDiceQuality(FantasyDice.CONFIG.diceStoneQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.COBBLESTONE))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.COBBLESTONE))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.COBBLESTONE))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.COBBLESTONE))
				.build()
			.build();
	// endregion

	// region: Bone
	public static final DiceType<FTRegistry, DiceItem> DICE_BONE = DiceType
			.builder("bone", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFEDDABC)))
				.withDiceQuality(FantasyDice.CONFIG.diceBoneQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.BONES))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.BONES))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.BONES))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.BONES))
				.build()
			.build();
	// endregion

	// region: Paper
	public static final DiceType<FTRegistry, DyeableDiceItem> DICE_PAPER = DiceType
			.builder("paper", REGISTRY, DyeableDiceItem::new)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.WHITE))
				.withDiceQuality(FantasyDice.CONFIG.dicePaperQuality::get)

				.withDie(4)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.PAPER))
				.build()

				.withDie(6)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.PAPER))
				.build()

				.withDie(8)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.PAPER))
				.build()

				.withDie(20)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.PAPER))
				.build()
			.build();
	// endregion

	// region: Iron
	public static final DiceType<FTRegistry, DiceItem> DICE_IRON = DiceType
			.builder("iron", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFE0E0E0)))
				.withDiceQuality(FantasyDice.CONFIG.diceIronQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_IRON))
				.build()
			.build();
	// endregion

	// region: Golden
	public static final DiceType<FTRegistry, DiceItem> DICE_GOLD = DiceType
			.builder("golden", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFE8C037)))
				.withDiceQuality(FantasyDice.CONFIG.diceGoldenQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_GOLD))
				.build()
			.build();
	// endregion

	// region: Diamond
	public static final DiceType<FTRegistry, DiceItem> DICE_DIAMOND = DiceType
			.builder("diamond", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.AQUA))
				.withDiceQuality(FantasyDice.CONFIG.diceDiamondQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_DIAMOND))
				.build()
			.build();
	// endregion

	// region: Emerald
	public static final DiceType<FTRegistry, DiceItem> DICE_EMERALD = DiceType
			.builder("emerald", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF1F8B20)))
				.withDiceQuality(FantasyDice.CONFIG.diceEmeraldQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.GEMS_EMERALD))
				.build()
			.build();
	// endregion

	// region: Netherite
	public static final DiceType<FTRegistry, DiceItem> DICE_NETHERITE = DiceType
			.builder("netherite", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF423030)))
				.withDiceQuality(FantasyDice.CONFIG.diceNetheriteQuality::get)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_NETHERITE))
					.fireResistant()
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_NETHERITE))
					.fireResistant()
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_NETHERITE))
					.fireResistant()
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.INGOTS_NETHERITE))
					.fireResistant()
				.build()
			.build();
	// endregion
	// endregion

	// region: Cosmetic
	// region: Ender
	public static final DiceType<FTRegistry, DiceItem> DICE_ENDER = DiceType
			.builder("ender", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF0F5959)))
				.withType(DiceType.Type.COSMETIC)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.ENDER_PEARL))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.ENDER_PEARL))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.ENDER_PEARL))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.ENDER_PEARL))
				.build()
			.build();
	// endregion

	// region: Frozen
	public static final DiceType<FTRegistry, DiceItem> DICE_FROZEN = DiceType
			.builder("frozen", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFBADED8)))
				.withType(DiceType.Type.COSMETIC)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.ICE))
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.ICE))
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.ICE))
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Blocks.ICE))
				.build()
			.build();
	// endregion

	// region: Slime
	public static final DiceType<FTRegistry, DiceItem> DICE_SLIME = DiceType
			.builder("slime", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF8ACC83)))
				.withType(DiceType.Type.COSMETIC)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.SLIMEBALLS))
					.fireResistant()
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.SLIMEBALLS))
					.fireResistant()
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.SLIMEBALLS))
					.fireResistant()
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.SLIMEBALLS))
					.fireResistant()
				.build()
			.build();
	// endregion

	// region: Redstone
	public static final DiceType<FTRegistry, DiceItem> DICE_REDSTONE = DiceType
			.builder("redstone", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF931515)))
				.withType(DiceType.Type.COSMETIC)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.DUSTS_REDSTONE))
					.fireResistant()
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.DUSTS_REDSTONE))
					.fireResistant()
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.DUSTS_REDSTONE))
					.fireResistant()
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Tags.Items.DUSTS_REDSTONE))
					.fireResistant()
				.build()
			.build();
	// endregion
	// endregion

	// region: Speciality
	// region: Fantasy
	public static final DiceType<FTRegistry, DiceItem> DICE_FANTASY = DiceType
			.builder("fantasy", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFF39F9F)))
				.withType(DiceType.Type.SPECIALITY)
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF5B20A2)))
				.withType(DiceType.Type.SPECIALITY)
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, TextFormatting.DARK_PURPLE))
				.withType(DiceType.Type.SPECIALITY)
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
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFFFF681F)))
				.withType(DiceType.Type.SPECIALITY)
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

	// region: Chocolate
	public static final DiceType<FTRegistry, DiceItem> DICE_CHOCOLATE = DiceType
			.builder("chocolate", REGISTRY)
				.withStyle((stack, style) -> colorOrDyed(stack, style, Color.fromRgb(0xFF673B27)))
				.withType(DiceType.Type.COSMETIC)

				.withDie(4)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.COCOA_BEANS))
					.fireResistant()
				.build()

				.withDie(6)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.COCOA_BEANS))
					.fireResistant()
				.build()

				.withDie(8)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.COCOA_BEANS))
					.fireResistant()
				.build()

				.withDie(20)
					.recipe((ctx, provider) -> diceRecipe(ctx, provider, Items.COCOA_BEANS))
					.fireResistant()
				.build()
			.build();
	// endregion
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
	private static <D extends DiceItem> void diceRecipe(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, ITag.INamedTag<Item> ingredient)
	{
		FTRecipes.diceStation(DataIngredient.tag(ingredient), ctx::get, 1)
		         .unlocks("has_item", RegistrateRecipeProvider.hasItem(ingredient))
				 // .group(String.format("dice/%d_sided", ctx.getEntry().getSides()))
		         .save(provider, ctx.getId());
	}

	private static <D extends DiceItem> void diceRecipe(DataGenContext<Item, D> ctx, RegistrateRecipeProvider provider, IItemProvider ingredient)
	{
		FTRecipes.diceStation(DataIngredient.items(ingredient.asItem()), ctx::get, 1)
		         .unlocks("has_item", RegistrateRecipeProvider.hasItem(ingredient))
		         // .group(String.format("dice/%d_sided", ctx.getEntry().getSides()))
		         .save(provider, ctx.getId());
	}
	// endregion
}
