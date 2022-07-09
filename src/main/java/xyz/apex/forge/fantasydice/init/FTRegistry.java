package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import org.apache.commons.lang3.Validate;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import xyz.apex.forge.commonality.Mods;
import xyz.apex.forge.fantasydice.FantasyDice;

import java.util.Arrays;

public final class FTRegistry extends AbstractRegistrate<FTRegistry>
{
	public static final FTRegistry INSTANCE = new FTRegistry();
	private static boolean bootstrap = false;

	private FTRegistry()
	{
		super(Mods.FANTASY_DICE);

		creativeModeTab(ModItemGroup::new, "Fantasy's Dice");

		addDataGenerator(ProviderType.LANG, provider -> {
			provider.add(FantasyDice.DIE_ROLL_KEY, "%s rolls %s");
			provider.add(FantasyDice.DIE_ROLL_RESULT_KEY, "%s (%sd%s)");
			provider.add(FantasyDice.JEI_DICE_RECIPE_TITLE_KEY, "Dice Station");

			provider.add(FantasyDice.COIN_FLIP_PREFIX, "%s flipped");
			provider.add(FantasyDice.COIN_FLIP_SUFFIX, "%s Heads and %s Tails");
			provider.add(FantasyDice.COIN_DESC, "Flip to get Heads or Tails");

			Arrays.stream(DiceType.Type.VALUES).forEach(t -> provider.add(t.getTranslationKey(), RegistrateLangProvider.toEnglishName(t.name())));
		});
	}

	public static void bootstrap()
	{
		if(bootstrap)
			return;

		Validate.isTrue(ModLoadingContext.get().getActiveContainer().getModId().equals(Mods.FANTASY_DICE));

		INSTANCE.registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());

		FTDiceTypes.bootstrap();
		FTItems.bootstrap();
		FTBlocks.bootstrap();
		FTMenus.bootstrap();
		FTTags.bootstrap();
		FTRecipes.bootstrap();

		bootstrap = true;
	}

	public static final class ModItemGroup extends CreativeModeTab
	{
		public ModItemGroup()
		{
			super(Mods.FANTASY_DICE);
		}

		@Override
		public ItemStack makeIcon()
		{
			return FTDiceTypes.DICE_GOLD.getItem(20).asStack();
		}
	}
}