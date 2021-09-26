package xyz.apex.forge.dicemod.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

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
		if(stack.getItem().is(DiceMod.DICE_TWENTY_SIDED))
			return 20;
		if(stack.getItem().is(DiceMod.DICE_SIX_SIDED))
			return 6;
		return 6;
	}

	public static boolean throwDice(World world, PlayerEntity thrower, Hand hand, int min)
	{
		ItemStack die = thrower.getItemInHand(hand);

		if(die.getItem().is(DiceMod.DICE))
		{
			if(throwDice(world, thrower, hand, die, min))
			{
				thrower.getCooldowns().addCooldown(die.getItem(), DiceMod.SERVER_CONFIG.getDiceCooldown());
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

		DiceMod.sendMessageToPlayers(thrower, dice.createTextComponent(thrower, die, totalRolls));

		return true;
	}
}
