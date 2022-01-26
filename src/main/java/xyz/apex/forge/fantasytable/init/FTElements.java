package xyz.apex.forge.fantasytable.init;

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

import xyz.apex.forge.fantasytable.item.DiceItem;
import xyz.apex.forge.fantasytable.item.DyeableDiceItem;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;

import javax.annotation.Nullable;

public final class FTElements
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	// region: Wooden
	public static final DiceType<FTRegistry, DiceItem> DICE_WOODEN = DiceType
			.builder("wooden", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, Color.fromRgb(0xFF8A5A27)))
				.withRecipe((sides, ctx, recipe) -> recipes(sides, recipe, ItemTags.WOODEN_BUTTONS))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Stone
	public static final DiceType<FTRegistry, DiceItem> DICE_STONE = DiceType
			.builder("stone", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.GRAY))
				.withRecipe((sides, ctx, recipe) -> recipes(sides, recipe, Blocks.STONE_BUTTON))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Paper
	public static final DiceType<FTRegistry, DyeableDiceItem> DICE_PAPER = DiceType
			.builder("paper", REGISTRY, DyeableDiceItem::new)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.WHITE))
				.withRecipe((sides, ctx, recipe) -> recipes(sides, recipe, Items.PAPER))

				.withDie(6)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
				.build()

				.withDie(20)
					.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack))
				.build()
			.build();
	// endregion

	// region: Bone
	public static final DiceType<FTRegistry, DiceItem> DICE_BONE = DiceType
			.builder("bone", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.WHITE))
				.withRecipe((sides, ctx, recipe) -> recipes(sides, recipe, Tags.Items.BONES))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Iron
	public static final DiceType<FTRegistry, DiceItem> DICE_IRON = DiceType
			.builder("iron", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.GRAY))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Golden
	public static final DiceType<FTRegistry, DiceItem> DICE_GOLD = DiceType
			.builder("golden", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.YELLOW))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Diamond
	public static final DiceType<FTRegistry, DiceItem> DICE_DIAMOND = DiceType
			.builder("diamond", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.AQUA))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Emerald
	public static final DiceType<FTRegistry, DiceItem> DICE_EMERALD = DiceType
			.builder("emerald", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.GREEN))
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	// region: Creative
	public static final DiceType<FTRegistry, DiceItem> DICE_CREATIVE = DiceType
			.builder("creative", REGISTRY)
				.withNameStyle((stack, style) -> nameStyle(stack, style, TextFormatting.LIGHT_PURPLE))
				.usesFoil()
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();
	// endregion

	static void bootstrap()
	{
	}

	// region: Styles
	private static Style nameStyle(ItemStack stack, Style style, Color color)
	{
		Item item = stack.getItem();

		if(!stack.isEmpty() && item instanceof IDyeableArmorItem)
		{
			int dyedColor = ((IDyeableArmorItem) item).getColor(stack);
			return style.withColor(Color.fromRgb(dyedColor));
		}

		return style.withColor(color);
	}

	private static Style nameStyle(ItemStack stack, Style style, TextFormatting formatting)
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
	@Nullable
	private static ShapedRecipeBuilder recipes(int sides, ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		switch(sides)
		{
			case 6: return recipeSixSided(recipe, ingredient);
			case 20: return recipeTwentySided(recipe, ingredient);
			default: return null;
		}
	}

	private static ShapedRecipeBuilder recipeSixSided(ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern("II")
		             .pattern("II")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	private static ShapedRecipeBuilder recipeTwentySided(ShapedRecipeBuilder recipe, ITag.INamedTag<Item> ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern(" I ")
		             .pattern("III")
		             .pattern(" I ")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	@Nullable
	private static ShapedRecipeBuilder recipes(int sides, ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		switch(sides)
		{
			case 6: return recipeSixSided(recipe, ingredient);
			case 20: return recipeTwentySided(recipe, ingredient);
			default: return null;
		}
	}

	private static ShapedRecipeBuilder recipeSixSided(ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern("II")
		             .pattern("II")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}

	private static ShapedRecipeBuilder recipeTwentySided(ShapedRecipeBuilder recipe, IItemProvider ingredient)
	{
		return recipe.define('I', ingredient)
		             .pattern(" I ")
		             .pattern("III")
		             .pattern(" I ")
		             .unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ingredient));
	}
	// endregion
}
