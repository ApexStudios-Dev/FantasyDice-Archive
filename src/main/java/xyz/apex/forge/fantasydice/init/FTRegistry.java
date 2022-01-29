package xyz.apex.forge.fantasydice.init;

import org.apache.commons.lang3.Validate;

import net.minecraftforge.fml.ModLoadingContext;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.utility.registrator.AbstractRegistrator;
import xyz.apex.forge.utility.registrator.helper.RegistratorItemGroup;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.java.utility.Lazy;
import xyz.apex.repack.com.tterrag.registrate.providers.ProviderType;

public final class FTRegistry extends AbstractRegistrator<FTRegistry>
{
	private static final Lazy<FTRegistry> registry = AbstractRegistrator.create(FTRegistry::new);
	private static boolean bootstrap = false;

	private FTRegistry()
	{
		super(FantasyDice.ID);

		skipErrors();
		itemGroup(() -> RegistratorItemGroup.create(this), "Fantasy's Dice");

		addDataGenerator(ProviderType.LANG, provider -> {
			provider.add(FantasyDice.DIE_ROLL_KEY, "%s rolls %s");
			provider.add(FantasyDice.DIE_ROLL_RESULT_KEY, "%s (%sd%s)");
			provider.add(FantasyDice.DIE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
			provider.add(FantasyDice.DIE_APEX_NAME, "Apex's %s %s-Sided Die");
		});

		addDataGenerator(LANG_EXT_PROVIDER, provider -> {
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_KEY, "%s rolls %s");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_RESULT_KEY, "%s (%sd%s)");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
			provider.add(RegistrateLangExtProvider.EN_GB, FantasyDice.DIE_APEX_NAME, "Apex's %s %s-Sided Die");
		});
	}

	public static void bootstrap()
	{
		if(bootstrap)
			return;

		Validate.isTrue(ModLoadingContext.get().getActiveContainer().getModId().equals(FantasyDice.ID));

		FTDiceTypes.bootstrap();

		FTTags.bootstrap();

		bootstrap = true;
	}

	public static FTRegistry getRegistry()
	{
		return registry.get();
	}
}
