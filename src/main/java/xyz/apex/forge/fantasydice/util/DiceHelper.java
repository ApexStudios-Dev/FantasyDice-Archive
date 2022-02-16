package xyz.apex.forge.fantasydice.util;

import net.minecraft.network.chat.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.item.DiceItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class DiceHelper
{
	@Nullable private static MutableComponent apexNameComponent = null;

	public static int roll(Random rng, int min, int max, int dieQuality, boolean isApex)
	{
		if(max < min)
		{
			var tmp = max;
			max = min;
			min = tmp;
		}

		var maxOriginal = max;
		var minOriginal = min;

		/*if(loaded)
			min = Math.max(1, max / 2);*/

		if(dieQuality > 0)
		{
			var newMax = rng.nextInt(dieQuality);

			if(newMax != 0)
				max += newMax;
		}
		else if(dieQuality < 0)
		{
			var newMax = rng.nextInt(Math.abs(dieQuality));

			if(newMax != 0)
				max -= newMax;
		}

		if(!isApex)
			min = Math.max(min, minOriginal);

		max = Math.min(max, maxOriginal);

		var roll = rng.nextInt(max) + 1;

		if(roll < min)
		{
			while(roll < min)
			{
				roll = rng.nextInt(max) + 1;
			}
		}

		return roll;
	}

	public static boolean throwDice(Level level, Player player, InteractionHand hand, ItemStack stack, int min)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		var die = (DiceItem) stack.getItem();
		var sides = die.getSides();
		var stackCount = stack.getCount();
		var maxPossibleRoll = sides * stackCount;
		var diceType = die.getDiceType();
		var dieQuality = diceType.getDiceQuality();
		var isApex = diceType.matches(FTDiceTypes.DICE_APEX);
		var rolls = IntStream.range(0, stackCount).map(i -> roll(level.random, min, sides, dieQuality, isApex)).toArray();
		rolls = diceType.onRoll(player, hand, stack, min, sides, rolls);
		var roll = Arrays.stream(rolls).sum();
		// roll += diceType.getDiceQuality();

		if(!isApex) // apex goes negative, clamping will break it
			roll = Mth.clamp(roll, min, maxPossibleRoll);

		var textComponent = createTextComponent(player, stack, die, roll, sides, rolls);
		sendMessageToPlayers(player, textComponent);

		return true;
	}

	private static MutableComponent createTextComponent(Player player, ItemStack stack, DiceItem die, int roll, int sides, int[] rolls)
	{
		var diceType = die.getDiceType();

		return new TranslatableComponent(
				FantasyDice.DIE_ROLL_KEY,
				player.getDisplayName(),
				new TranslatableComponent(FantasyDice.DIE_ROLL_RESULT_KEY, roll, stack.getCount(), sides).withStyle(style -> diceType.withStyle(stack, style))
		).withStyle(style -> style
				.withHoverEvent(
						new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								stack.getHoverName().copy()
								     .append(new TextComponent("\n" + Arrays.toString(rolls)).withStyle(s->s.withItalic(true)))
								     .withStyle(hoverStyle -> diceType.withStyle(stack, hoverStyle))
						)
				)
		);
	}

	public static void sendMessageToPlayers(Player player, Component component)
	{
		var server = player.getServer();
		var playerID = player.getGameProfile().getId();

		player.sendMessage(component, playerID);

		if(server == null)
			return;

		var dimensionType = player.level.dimensionType();
		var chunkPos = new ChunkPos(player.blockPosition());

		for(var plr : server.getPlayerList().getPlayers())
		{
			if(plr.getGameProfile().getId().equals(playerID))
				continue;

			if(dimensionType.equalTo(plr.level.dimensionType()))
			{
				if(chunkPos.getChessboardDistance(new ChunkPos(plr.blockPosition())) > FantasyDice.CONFIG.diceRollMessageRange.get())
					continue;
			}
			else
			{
				if(!FantasyDice.CONFIG.diceRollMessageCrossDimensions.get())
					continue;
			}

			plr.sendMessage(component, playerID);
		}
	}

	public static boolean isLuckyRoller(Player player)
	{
		var playerId = player.getGameProfile().getId();

		// should always work for FantasyGaming
		if(playerId.equals(FantasyDice.FANTASY_UUID))
			return true;

		return FantasyDice.CONFIG.luckyRollerIDs.contains(playerId);
	}

	public static MutableComponent makeApexComponent(Random rng, Component component)
	{
		if(apexNameComponent != null)
			return apexNameComponent;

		var apex = TextComponent.EMPTY.plainCopy();
		var string = component.getString();

		for(var c : string.toCharArray())
		{
			var obfuscate = rng.nextBoolean();
			apex = apex.append(new TextComponent(String.valueOf(c)).withStyle(style -> style.setObfuscated(obfuscate)));
		}

		if(FantasyDice.loadComplete && apexNameComponent == null)
			apexNameComponent = apex;

		return apex;
	}
}
