package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import xyz.apex.forge.apexcore.lib.item.crafting.SingleItemRecipe;
import xyz.apex.forge.commonality.Mods;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

public final class FTRecipes
{
	public static final RegistryEntry<SingleItemRecipe.Serializer<DiceStationRecipe>> DICE_STATION_RECIPE = FTRegistry
			.REGISTRATE
			.object("dice_station")
			.simple(Registries.RECIPE_SERIALIZER, () -> new SingleItemRecipe.Serializer<>(DiceStationRecipe::new))
	;

	public static final RegistryEntry<RecipeType<DiceStationRecipe>> DICE_STATION_RECIPE_TYPE = FTRegistry
			.REGISTRATE
			.object("dice_station")
			.simple(Registries.RECIPE_TYPE, () -> RecipeType.simple(new ResourceLocation(Mods.FANTASY_DICE, "dice_station")))
	;

	static void bootstrap()
	{
	}

	public static SingleItemRecipeBuilder diceStation(DataIngredient ingredient, ItemLike result, int count)
	{
		return new SingleItemRecipeBuilder(RecipeCategory.MISC, DICE_STATION_RECIPE.get(), ingredient, result, count);
	}
}
