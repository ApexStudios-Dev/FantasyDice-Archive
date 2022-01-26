package xyz.apex.forge.fantasytable;

import net.minecraftforge.fml.common.Mod;

import xyz.apex.forge.fantasytable.init.FTRegistry;

@Mod(FantasyTable.ID)
public final class FantasyTable
{
	public static final String ID = "fantasytable";

	public static final String DIE_ROLL_KEY = ID + ".die.roll";
	public static final String DIE_ROLL_RESULT_KEY = ID + ".die.roll.result";
	public static final String DIE_ROLL_USING_KEY = ID + ".die.using";
	public static final String DIE_ROLL_DESC_KEY = ID + ".die.roll.desc";

	public FantasyTable()
	{
		FTRegistry.bootstrap();
	}
}
