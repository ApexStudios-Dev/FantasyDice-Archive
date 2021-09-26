package xyz.apex.forge.dicemod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import xyz.apex.forge.dicemod.Dice;

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
