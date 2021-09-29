package xyz.apex.forge.fantasytable.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.Coins;
import xyz.apex.forge.fantasytable.init.Dice;
import xyz.apex.forge.fantasytable.init.FTags;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

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
		if(stack.getItem().is(FTags.Items.COINS))
			return 2;
		if(stack.getItem().is(FTags.Items.DICE_TWENTY_SIDED))
			return 20;
		if(stack.getItem().is(FTags.Items.DICE_SIX_SIDED))
			return 6;
		return 6;
	}

	public static boolean throwDice(World world, PlayerEntity thrower, Hand hand, ItemStack die, int min, int sides)
	{
		if(die.isEmpty())
			return false;
		if(world.isClientSide)
			return true;

		int[] rolls = IntStream.range(0, die.getCount()).map(i -> roll(world.random, min, sides)).toArray();
		sendMessageToPlayers(thrower, createTextComponent(thrower, die, rolls, min, sides));
		return true;
	}

	public static IFormattableTextComponent createTextComponent(PlayerEntity thrower, ItemStack die, int[] rolls, int min, int sides)
	{
		if(die.getItem().is(FTags.Items.COINS))
		{
			Coins coin = Coins.byItem(die);
			return coin.createTextComponent(thrower, die, rolls);
		}
		else
		{
			int totalRolls = Arrays.stream(rolls).sum();

			if(die.getItem().is(FTags.Items.DICE))
			{
				Dice dice = Dice.byItem(die);
				return dice.createTextComponent(thrower, die, totalRolls);
			}
			else
				return Dice.PAPER.createTextComponent(thrower, die, totalRolls);
		}
	}

	public static IFormattableTextComponent createItemTooltipComponent(ItemStack die, int min, int sides)
	{
		if(die.getItem().is(FTags.Items.COINS))
		{
			Coins coin = Coins.byItem(die);
			return coin.createItemTooltipComponent(die, min, sides);
		}
		else if(die.getItem().is(FTags.Items.DICE))
		{
			Dice dice = Dice.byItem(die);
			return dice.createItemTooltipComponent(die, min, sides);
		}
		else
			return Dice.PAPER.createItemTooltipComponent(die, min, sides);
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
