package xyz.apex.forge.fantasydice.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import xyz.apex.forge.apexcore.lib.item.crafting.SingleItemRecipe;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;

public final class DiceStationRecipe extends SingleItemRecipe
{
	public DiceStationRecipe(ResourceLocation recipeId, String recipeGroup, Ingredient recipeIngredient, ItemStack recipeResult)
	{
		super(FTRecipes.DICE_STATION_RECIPE_TYPE.get(), FTRecipes.DICE_STATION_RECIPE.get(), recipeId, recipeGroup, recipeIngredient, recipeResult);
	}

	@Override
	public ItemStack getToastSymbol()
	{
		return FTBlocks.DICE_STATION.asStack();
	}
}
