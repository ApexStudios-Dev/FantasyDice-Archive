/*
package xyz.apex.forge.fantasydice.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import xyz.apex.forge.commonality.Mods;
import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

@JeiPlugin
public class JeiIntegration implements IModPlugin
{
	public static final RecipeType<DiceStationRecipe> RECIPE_TYPE = RecipeType.create(Mods.FANTASY_DICE, "dice_station", DiceStationRecipe.class);

	private DiceStationRecipeCategory diceStationRecipeCategory;

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(Mods.FANTASY_DICE, "jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		var guiHelper = registration.getJeiHelpers().getGuiHelper();
		diceStationRecipeCategory = new DiceStationRecipeCategory(guiHelper);
		registration.addRecipeCategories(diceStationRecipeCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		var recipeManager = Minecraft.getInstance().level.getRecipeManager();
		var diceStationRecipes = recipeManager.getAllRecipesFor(FTRecipes.DICE_STATION_RECIPE_TYPE.get());
		registration.addRecipes(RECIPE_TYPE, diceStationRecipes);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(FTBlocks.DICE_STATION.asStack(), RECIPE_TYPE);
	}

	public static class DiceStationRecipeCategory implements IRecipeCategory<DiceStationRecipe>
	{
		private final IDrawable background;
		private final IDrawable icon;

		private DiceStationRecipeCategory(IGuiHelper guiHelper)
		{
			background = guiHelper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 0, 220, 82, 34);
			icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, FTBlocks.DICE_STATION.asStack());
		}

		@Override
		public RecipeType<DiceStationRecipe> getRecipeType()
		{
			return RECIPE_TYPE;
		}

		@Override
		public Component getTitle()
		{
			return Component.translatable(FantasyDice.JEI_DICE_RECIPE_TITLE_KEY);
		}

		@Override
		public IDrawable getBackground()
		{
			return background;
		}

		@Override
		public IDrawable getIcon()
		{
			return icon;
		}

		@Override
		public void setRecipe(IRecipeLayoutBuilder builder, DiceStationRecipe recipe, IFocusGroup focuses)
		{
			builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
			       .addIngredients(recipe.getIngredients().get(0));

			builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9)
			       .addItemStack(recipe.getResultItem());
		}

		@Override
		public boolean isHandled(DiceStationRecipe recipe)
		{
			return !recipe.isSpecial();
		}
	}
}*/