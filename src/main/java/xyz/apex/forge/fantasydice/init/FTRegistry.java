package xyz.apex.forge.fantasydice.init;

import org.apache.commons.lang3.Validate;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.utility.registrator.AbstractRegistrator;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.java.utility.Lazy;
import xyz.apex.repack.com.tterrag.registrate.providers.ProviderType;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateLangProvider;

import java.util.Arrays;

public final class FTRegistry extends AbstractRegistrator<FTRegistry>
{
	private static final Lazy<FTRegistry> registry = AbstractRegistrator.create(FTRegistry::new);
	private static boolean bootstrap = false;

	private FTRegistry()
	{
		super(FantasyDice.ID);

		//skipErrors();
		creativeModeTab(ItemGroup::new, "Fantasy's Dice");

		addDataGenerator(ProviderType.LANG, provider -> {
			provider.add(FantasyDice.DIE_ROLL_KEY, "%s rolls %s");
			provider.add(FantasyDice.DIE_ROLL_RESULT_KEY, "%s (%sd%s)");
			provider.add(FantasyDice.DIE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
			provider.add(FantasyDice.JEI_DICE_RECIPE_TITLE_KEY, "Dice Station");

			provider.add(FantasyDice.COIN_FLIP, "%s flipped %s Heads and %s Tails");
			provider.add(FantasyDice.COIN_DESC, "Flip it and see if you will get Heads or Tails");

			Arrays.stream(DiceType.Type.VALUES).forEach(t -> provider.add(t.getTranslationKey(), RegistrateLangProvider.toEnglishName(t.name())));
		});

		addDataGenerator(LANG_EXT_PROVIDER, provider -> {
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_KEY, "%s rolls %s");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_RESULT_KEY, "%s (%sd%s)");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.JEI_DICE_RECIPE_TITLE_KEY, "Dice Station");

			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.COIN_FLIP, "%s flipped %s Heads and %s Tails");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.COIN_DESC, "Flip it and see if you will get Heads or Tails");

			Arrays.stream(DiceType.Type.VALUES).forEach(t -> provider.add(RegistrateLangExtProvider.EN_GB, t.getTranslationKey(), RegistrateLangProvider.toEnglishName(t.name())));
		});
	}

	public static void bootstrap()
	{
		if(bootstrap)
			return;

		Validate.isTrue(ModLoadingContext.get().getActiveContainer().getModId().equals(FantasyDice.ID));

		FTDiceTypes.bootstrap();
		FTItems.bootstrap();
		FTBlocks.bootstrap();
		FTMenus.bootstrap();
		FTTags.bootstrap();
		FTRecipes.bootstrap();

		bootstrap = true;
	}

	public static FTRegistry getRegistry()
	{
		return registry.get();
	}

	public static final class ItemGroup extends CreativeModeTab
	{
		public ItemGroup()
		{
			super(FantasyDice.ID);
		}

		@Override
		public ItemStack makeIcon()
		{
			return FTDiceTypes.DICE_GOLD.getItem(20).asItemStack();
		}
	}
}
