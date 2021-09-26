package xyz.apex.forge.dicemod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

import java.util.function.Consumer;

public final class RecipeGenerator extends RecipeProvider
{
	public RecipeGenerator(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
	{
		ShapedRecipeBuilder.shaped(DiceMod.POUCH::get, 1)
		                   .define('S', Tags.Items.STRING)
		                   .define('L', Tags.Items.LEATHER)
		                   .pattern(" S ")
		                   .pattern("L L")
		                   .pattern(" L ")
		                   .unlockedBy("has_leather", has(Tags.Items.LEATHER))
		                   .unlockedBy("has_string", has(Tags.Items.STRING))
		                   .save(consumer, new ResourceLocation(DiceMod.ID, "dice_pouch"));

		for(Dice dice : Dice.TYPES)
		{
			dice.onGenerateRecipes(consumer, RecipeProvider::has, RecipeProvider::has);
		}
	}

	@Override
	public String getName()
	{
		return "DiceMod, RecipeGenerator";
	}
}
