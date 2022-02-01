package xyz.apex.forge.fantasydice.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

import java.util.List;

@JeiPlugin
public class JeiIntegration implements IModPlugin
{
	private DiceStationRecipeCategory diceStationRecipeCategory;

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(FantasyDice.ID, "jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		diceStationRecipeCategory = new DiceStationRecipeCategory(guiHelper);
		registration.addRecipeCategories(diceStationRecipeCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		List<DiceStationRecipe> diceStationRecipes = recipeManager.getAllRecipesFor(FTRecipes.DICE_STATION_RECIPE.asRecipeType());
		registration.addRecipes(diceStationRecipes, FTRecipes.DICE_STATION_RECIPE.getId());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(FTBlocks.DICE_STATION.asItemStack(), FTRecipes.DICE_STATION_RECIPE.getId());
	}

	public static class DiceStationRecipeCategory implements IRecipeCategory<DiceStationRecipe>
	{
		private final IDrawable background;
		private final IDrawable icon;

		private DiceStationRecipeCategory(IGuiHelper guiHelper)
		{
			background = guiHelper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 0, 220, 82, 34);
			icon = guiHelper.createDrawableIngredient(FTBlocks.DICE_STATION.asItemStack());
		}

		@Override
		public ResourceLocation getUid()
		{
			return FTRecipes.DICE_STATION_RECIPE.getId();
		}

		@Override
		public Class<? extends DiceStationRecipe> getRecipeClass()
		{
			return DiceStationRecipe.class;
		}

		@Override
		public ITextComponent getTitleAsTextComponent()
		{
			return new TranslationTextComponent(FantasyDice.JEI_DICE_RECIPE_TITLE_KEY);
		}

		@Override
		public String getTitle()
		{
			return getTitleAsTextComponent().getString();
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
		public void setIngredients(DiceStationRecipe recipe, IIngredients ingredients)
		{
			ingredients.setInputIngredients(recipe.getIngredients());
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, DiceStationRecipe recipe, IIngredients ingredients)
		{
			IGuiItemStackGroup group = recipeLayout.getItemStacks();
			group.init(0, true, 0, 8);
			group.init(1, false, 60, 8);
			group.set(ingredients);
		}

		@Override
		public boolean isHandled(DiceStationRecipe recipe)
		{
			return !recipe.isSpecial();
		}
	}
}
