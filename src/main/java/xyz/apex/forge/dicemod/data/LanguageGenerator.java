package xyz.apex.forge.dicemod.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

public final class LanguageGenerator extends LanguageProvider
{
	public static final String DICE_ROLL_KEY = "dicemod.dice.roll";

	public LanguageGenerator(DataGenerator generator)
	{
		super(generator, DiceMod.ID, "en_us");
	}

	@Override
	protected void addTranslations()
	{
		add("itemGroup." + DiceMod.ID, "Dice");
		add(DICE_ROLL_KEY, "%s rolls %dx %s");

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
