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
	public static final UUID TOBI_UUID = UUID.fromString("ae3f6ca6-b28c-479b-9c97-4be7df600041");
	public static final UUID APEX_UUID = UUID.fromString("43fd393b-879d-45ec-b2d5-ce8c4688ab66");

	public FantasyTable()
	{
		FTRegistry.bootstrap();
	}
}
