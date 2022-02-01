package xyz.apex.forge.fantasydice.init;

import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.level.ItemLike;

import xyz.apex.forge.apexcore.lib.item.crafting.SingleItemRecipe;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;
import xyz.apex.forge.utility.registrator.entry.RecipeSerializerEntry;
import xyz.apex.repack.com.tterrag.registrate.util.DataIngredient;

public final class FTRecipes
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final RecipeSerializerEntry<SingleItemRecipe.Serializer<DiceStationRecipe>, DiceStationRecipe> DICE_STATION_RECIPE = REGISTRY.recipeSerializer("dice_station", () -> new SingleItemRecipe.Serializer<DiceStationRecipe>(DiceStationRecipe::new));

	static void bootstrap()
	{
	}

	public static SingleItemRecipeBuilder diceStation(DataIngredient ingredient, ItemLike result, int count)
	{
		return new SingleItemRecipeBuilder(DICE_STATION_RECIPE.asRecipeSerializer(), ingredient, result, count);
	}
}
