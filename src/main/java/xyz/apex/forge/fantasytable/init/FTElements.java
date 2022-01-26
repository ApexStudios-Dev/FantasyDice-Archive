package xyz.apex.forge.fantasytable.init;

import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;

import xyz.apex.forge.fantasytable.item.DiceItem;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;

import javax.annotation.Nullable;

public final class FTElements
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	// region: Wooden
	public static final DiceType<FTRegistry, DiceItem> DICE_WOODEN = DiceType
			.builder("wooden", REGISTRY)
				.withNameStyle(style -> style.withColor(Color.fromRgb(0xFF8A5A27)))
				.withRecipe((sides, ctx, recipe) -> diceRecipe(sides, recipe, ItemTags.WOODEN_BUTTONS))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Stone
	public static final DiceType<FTRegistry, DiceItem> DICE_STONE = DiceType
			.builder("stone", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.GRAY))
				.withRecipe((sides, ctx, recipe) -> diceRecipe(sides, recipe, Blocks.STONE_BUTTON))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Paper
	public static final DiceType<FTRegistry, DiceItem> DICE_PAPER = DiceType
			.builder("paper", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.WHITE))
				.withRecipe((sides, ctx, recipe) -> diceRecipe(sides, recipe, Items.PAPER))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Bone
	public static final DiceType<FTRegistry, DiceItem> DICE_BONE = DiceType
			.builder("bone", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.WHITE))
				.withRecipe((sides, ctx, recipe) -> diceRecipe(sides, recipe, Tags.Items.BONES))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Iron
	public static final DiceType<FTRegistry, DiceItem> DICE_IRON = DiceType
			.builder("iron", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.GRAY))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Golden
	public static final DiceType<FTRegistry, DiceItem> DICE_GOLD = DiceType
			.builder("golden", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.YELLOW))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Diamond
	public static final DiceType<FTRegistry, DiceItem> DICE_DIAMOND = DiceType
			.builder("diamond", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.AQUA))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Emerald
	public static final DiceType<FTRegistry, DiceItem> DICE_EMERALD = DiceType
			.builder("emerald", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.GREEN))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Creative
	public static final DiceType<FTRegistry, DiceItem> DICE_CREATIVE = DiceType
			.builder("creative", REGISTRY)
				.withNameStyle(style -> style.withColor(TextFormatting.LIGHT_PURPLE))
				.usesFoil()
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	static void bootstrap()
	{
	}

	// region: Recipes
	@Nullable
	private static ShapedRecipeBuilder diceRecipe(int sides, ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		switch(sides)
		{
			case 6: return diceRecipeSixSided(recipe, ingredient);
			case 20: return diceRecipeTwentySided(recipe, ingredient);
			default: return null;
		}
	}

	private static ShapedRecipeBuilder diceRecipeSixSided(ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern("II")
		             .pattern("II")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	private static ShapedRecipeBuilder diceRecipeTwentySided(ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern(" I ")
		             .pattern("III")
		             .pattern(" I ")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	@Nullable
	private static ShapedRecipeBuilder diceRecipe(int sides, ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		switch(sides)
		{
			case 6: return diceRecipeSixSided(recipe, ingredient);
			case 20: return diceRecipeTwentySided(recipe, ingredient);
			default: return null;
		}
	}

	private static ShapedRecipeBuilder diceRecipeSixSided(ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern("II")
		             .pattern("II")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	private static ShapedRecipeBuilder diceRecipeTwentySided(ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern(" I ")
		             .pattern("III")
		             .pattern(" I ")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}
	// endregion
}
