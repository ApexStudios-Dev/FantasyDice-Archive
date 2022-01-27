package xyz.apex.forge.fantasytable;

import net.minecraftforge.fml.common.Mod;

import xyz.apex.forge.fantasytable.init.FTRegistry;

import java.util.UUID;

@Mod(FantasyTable.ID)
public final class FantasyTable
{
	public static final String ID = "fantasytable";

	public static final String DIE_ROLL_KEY = ID + ".die.roll";
	public static final String DIE_ROLL_RESULT_KEY = ID + ".die.roll.result";
	public static final String DIE_ROLL_DESC_KEY = ID + ".die.roll.desc";

	public static final UUID FANTASY_UUID = UUID.fromString("598535bd-f330-4123-b4d0-c6e618390477");

	public FantasyTable()
	{
		FTRegistry.bootstrap();
	}
}
