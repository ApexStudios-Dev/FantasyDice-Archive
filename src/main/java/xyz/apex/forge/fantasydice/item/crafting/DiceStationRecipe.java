package xyz.apex.forge.fantasydice.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import xyz.apex.forge.apexcore.lib.item.crafting.SingleItemRecipe;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;

public final class DiceStationRecipe extends SingleItemRecipe
{
	public DiceStationRecipe(ResourceLocation recipeId, String recipeGroup, Ingredient recipeIngredient, ItemStack recipeResult)
	{
		super(FTRecipes.DICE_STATION_RECIPE.asRecipeType(), FTRecipes.DICE_STATION_RECIPE.asRecipeSerializer(), recipeId, recipeGroup, recipeIngredient, recipeResult);
	}

	@Override
	public ItemStack getToastSymbol()
	{
		return FTBlocks.DICE_STATION.asItemStack();
	}
}
