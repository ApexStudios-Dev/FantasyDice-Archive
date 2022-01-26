package xyz.apex.forge.fantasytable.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.Color;

import xyz.apex.forge.fantasytable.item.DiceItem;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;

public final class FTElements
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final DiceType<FTRegistry, DiceItem> DICE_WOODEN = DiceType
			.builder("wooden", REGISTRY)
				.withNameStyle(style -> style
						.withColor(Color.fromRgb(0xFF8A5A27))
				)
				.withRecipe((sides, ctx, recipe) -> {
					recipe = recipe.define('I', ItemTags.WOODEN_BUTTONS).unlockedBy("has_item", RegistrateRecipeProvider.hasItem(ItemTags.WOODEN_BUTTONS));

					if(sides == 6)
						return recipe.pattern("II").pattern("II");
					else if(sides == 20)
						return recipe.pattern(" I ").pattern("III").pattern(" I ");

					return null;
				})
				.withSimpleDie(6)
				.withSimpleDie(20)
			.build();

	static void bootstrap()
	{
	}
}
