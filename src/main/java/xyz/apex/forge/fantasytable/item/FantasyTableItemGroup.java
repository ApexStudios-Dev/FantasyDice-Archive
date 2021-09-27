package xyz.apex.forge.fantasytable.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.Dice;

public final class FantasyTableItemGroup extends ItemGroup
{
	private int cycleTime = 0;
	private boolean isSix = false;
	private int diceType = 0;

	public FantasyTableItemGroup()
	{
		super(FantasyTable.ID);
	}

	@Override
	public ItemStack getIconItem()
	{
		cycleTime++;

		if(cycleTime >= 75)
		{
			cycleTime = 0;

			if(isSix)
			{
				diceType = (diceType + 1) % Dice.TYPES.length;
				isSix = false;
			}
			else
				isSix = true;

			ObfuscationReflectionHelper.setPrivateValue(ItemGroup.class, this, makeIcon(), "field_151245_t");
		}

		return super.getIconItem();
	}

	@Override
	public ItemStack makeIcon()
	{
		return (isSix ? Dice.TYPES[diceType].six_sided_die : Dice.TYPES[diceType].twenty_sided_die).get().getDefaultInstance();
	}
}
