package xyz.apex.forge.fantasytable.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.DTags;
import xyz.apex.forge.fantasytable.init.Dice;

import java.util.Random;

public final class DiceHelper
{
	public static int roll(Random rng, int min, int max)
	{
		if(max < min)
		{
			int temp = max;
			max = min;
			min = temp;
		}

		min = Math.max(min, 0);
		max = Math.max(max, 1);

		int roll = rng.nextInt(max) + 1;

		if(roll < min)
		{
			while(roll < min)
			{
				roll = rng.nextInt(max) + 1;
			}
		}

		return roll;
	}

	public static int getSides(ItemStack stack)
	{
		if(stack.getItem().is(DTags.Items.DICE_TWENTY_SIDED))
			return 20;
		if(stack.getItem().is(DTags.Items.DICE_SIX_SIDED))
			return 6;
		return 6;
	}

	public static boolean throwDice(World world, PlayerEntity thrower, Hand hand, int min)
	{
		ItemStack die = thrower.getItemInHand(hand);

		if(die.getItem().is(DTags.Items.DICE))
		{
			if(throwDice(world, thrower, hand, die, min))
			{
				thrower.getCooldowns().addCooldown(die.getItem(), FantasyTable.SERVER_CONFIG.getDiceCooldown());
				return true;
			}
		}

		return false;
	}

	private static boolean throwDice(World world, PlayerEntity thrower, Hand hand, ItemStack die, int min)
	{
		if(die.isEmpty())
			return false;
		if(world.isClientSide)
			return true;

		Dice dice = Dice.byItem(die);
		int sides = getSides(die);
		int totalRolls = 0;

		for(int i = 0; i < die.getCount(); i++)
		{
			int roll = roll(world.random, min, sides);
			// DiceMod.sendMessageToPlayers(thrower, dice.createTextComponent(thrower, roll, min, sides));
			totalRolls += roll;
		}

		sendMessageToPlayers(thrower, dice.createTextComponent(thrower, die, totalRolls));
		return true;
	}

	public static void sendMessageToPlayers(PlayerEntity thrower, ITextComponent component)
	{
		MinecraftServer server = thrower.getServer();

		if(server == null) // singleplayer?
		{
			thrower.sendMessage(component, thrower.getUUID());
			return;
		}

		int rollRange = FantasyTable.SERVER_CONFIG.getDiceRollRange();

		for(PlayerEntity player : server.getPlayerList().getPlayers())
		{
			if(rollRange > 0)
			{
				if(!player.level.dimensionType().equalTo(thrower.level.dimensionType()))
					continue;
				if(player.distanceTo(thrower) > rollRange)
					continue;
			}

			player.sendMessage(component, thrower.getUUID());
		}
	}
}
