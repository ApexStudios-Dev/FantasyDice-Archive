package xyz.apex.forge.dicemod.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

public final class LanguageGenerator extends LanguageProvider
{
	public static final String DICE_ROLL_KEY = DiceMod.ID + ".dice.roll";
	public static final String DICE_ROLL_USING_KEY = DiceMod.ID + ".dice.using";
	public static final String DICE_ROLL_DESC_KEY = DiceMod.ID + ".dice.roll.desc";
	public static final String POUCH_SCREEN_TITLE_KEY = DiceMod.ID + ".pouch.title"; // narrator message for pouch screen

	public LanguageGenerator(DataGenerator generator)
	{
		super(generator, DiceMod.ID, "en_us");
	}

	@Override
	protected void addTranslations()
	{
		add("itemGroup." + DiceMod.ID, "Dice");
		add(DICE_ROLL_KEY, "%s rolls %s");
		add(DICE_ROLL_USING_KEY, "Using a %s");
		add(DICE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
		add(POUCH_SCREEN_TITLE_KEY, "Dice Pouch");

		addItem(DiceMod.POUCH, "Dice Pouch");

		for(Dice dice : Dice.TYPES)
		{
			dice.onGenerateLanguage(this::addItem, this::add);
		}
	}

	@Override
	public String getName()
	{
		return "DiceMod, LanguageGenerator";
	}
}
