package xyz.apex.minecraft.fantasydice.common.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.menu.DiceStationMenu;

public final class DiceStationRecipe extends SingleItemRecipe
{
    public DiceStationRecipe(ResourceLocation recipeId, String group, Ingredient ingredient, ItemStack result)
    {
        super(FantasyDice.DICE_STATION_RECIPE_TYPE.value(), FantasyDice.DICE_STATION_RECIPE_SERIALIZER.value(), recipeId, group, ingredient, result);
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        return ingredient.test(container.getItem(DiceStationMenu.SLOT_INPUT));
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return FantasyDice.DICE_STATION_BLOCK.asStack();
    }

    public static SingleItemRecipeBuilder builder(RecipeCategory category, Ingredient ingredient, ItemLike result)
    {
        return builder(category, ingredient, result, 1);
    }

    public static SingleItemRecipeBuilder builder(RecipeCategory category, Ingredient ingredient, ItemLike result, int count)
    {
        return new SingleItemRecipeBuilder(category, FantasyDice.DICE_STATION_RECIPE_SERIALIZER.value(), ingredient, result, count);
    }
}
