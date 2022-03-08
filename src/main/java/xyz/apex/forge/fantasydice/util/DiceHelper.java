package xyz.apex.forge.fantasydice.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.DiceType;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.item.DiceItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class DiceHelper
{
	@Nullable private static IFormattableTextComponent apexNameComponent = null;

	public static int roll(Random rng, int min, int max, int dieQuality, boolean isApex)
	{
		if(max < min)
		{
			int tmp = max;
			max = min;
			min = tmp;
		}

		int maxOriginal = max;
		int minOriginal = min;

		/*if(loaded)
			min = Math.max(1, max / 2);*/

		if(dieQuality > 0)
		{
			int newMax = rng.nextInt(dieQuality);

			if(newMax != 0)
			{
				max += newMax;
			}
		}
		else if(dieQuality < 0)
		{
			int newMax = rng.nextInt(Math.abs(dieQuality));

			if(newMax != 0)
			{
				max -= newMax;
			}
		}

		if(!isApex)
			min = Math.max(min, minOriginal);

		max = Math.min(max, maxOriginal);

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

	/*private static boolean throw10SidedDice(World level, PlayerEntity player, Hand hand, ItemStack stack, DiceItem die)
	{
		int singleRoll = rollDice(level, player, hand, stack, 1);


		return true;

//		// single
//		int roll1 = rollDice(level, player, hand, stack, 1);
//		IFormattableTextComponent roll1Component = createTextComponent(player, stack, die, roll1, 10, new int[] { roll1 });
//		sendMessageToPlayers(player, roll1Component);
//
//		// 10
//		int roll10 = rollDice(level, player, hand, stack, 1) * 10;
//		IFormattableTextComponent roll10Component = createTextComponent(player, stack, die, roll10, 10, new int[] { roll10 });
//		sendMessageToPlayers(player, roll10Component);
//
//		return true;
	}*/

	public static boolean throwDice(World level, PlayerEntity player, Hand hand, ItemStack stack, int min)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		DiceItem die = (DiceItem) stack.getItem();
		int sides = die.getSides();

		/*if(sides == 10)
			return throw10SidedDice(level, player, hand, stack, die);*/

		int stackCount = stack.getCount();
		int maxPossibleRoll = sides * stackCount;
		DiceType<?, ?> diceType = die.getDiceType();
		boolean isApex = diceType.matches(FTDiceTypes.DICE_APEX);

		int[] rolls = IntStream.range(0, stackCount).map(i -> rollDice(level, player, hand, stack, min)).toArray();
		int roll = Arrays.stream(rolls).sum();

		if(!isApex) // apex goes negative, clamping will break it
			roll = MathHelper.clamp(roll, min, maxPossibleRoll);

		IFormattableTextComponent textComponent = createTextComponent(player, stack, die, roll, sides, rolls);
		sendMessageToPlayers(player, textComponent);

		return true;
	}

	private static int rollDice(World level, PlayerEntity player, Hand hand, ItemStack stack, int min)
	{
		DiceItem die = (DiceItem) stack.getItem();
		int sides = die.getSides();
		DiceType<?, ?> diceType = die.getDiceType();
		int diceQuality = diceType.getDiceQuality();
		boolean isApex = diceType.matches(FTDiceTypes.DICE_APEX);
		int roll = roll(level.random, min, sides, diceQuality, isApex);
		roll = diceType.onRoll(player, hand, stack, min, sides, roll);

		if(!isApex) // apex goes negative, clamping will break it
			roll = MathHelper.clamp(roll, min, sides);

		return roll;
	}

	private static IFormattableTextComponent createTextComponent(PlayerEntity player, ItemStack stack, DiceItem die, int roll, int sides, @Nullable int[] rolls)
	{
		DiceType<?, ?> diceType = die.getDiceType();
		ITextComponent[] rollsComponent = new ITextComponent[] { StringTextComponent.EMPTY };

		if(rolls != null && rolls.length > 1)
			rollsComponent[0] = new StringTextComponent("\n" + Arrays.toString(rolls)).withStyle(s -> s.withItalic(true));

		return new TranslationTextComponent(
				FantasyDice.DIE_ROLL_KEY,
				player.getDisplayName(),
				new TranslationTextComponent(FantasyDice.DIE_ROLL_RESULT_KEY, roll, stack.getCount(), sides).withStyle(style -> diceType.withStyle(stack, style))
		).withStyle(style -> style
				.withHoverEvent(
						new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								stack.getHoverName().copy()
								     .append(rollsComponent[0])
								     .withStyle(hoverStyle -> diceType.withStyle(stack, hoverStyle))
						)
				)
		);
	}

	public static void sendMessageToPlayers(PlayerEntity player, ITextComponent component)
	{
		MinecraftServer server = player.getServer();
		UUID playerID = player.getGameProfile().getId();

		player.sendMessage(component, playerID);

		if(server == null)
			return;

		DimensionType dimensionType = player.level.dimensionType();
		ChunkPos chunkPos = new ChunkPos(player.blockPosition());

		for(PlayerEntity plr : server.getPlayerList().getPlayers())
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

	public static boolean isLuckyRoller(PlayerEntity player)
	{
		UUID playerId = player.getGameProfile().getId();

		// should always work for FantasyGaming
		if(playerId.equals(FantasyDice.FANTASY_UUID))
			return true;

		return FantasyDice.CONFIG.luckyRollerIDs.contains(playerId);
	}

	public static IFormattableTextComponent makeApexComponent(Random rng, ITextComponent component)
	{
		if(apexNameComponent != null)
			return apexNameComponent;

		IFormattableTextComponent apex = StringTextComponent.EMPTY.plainCopy();
		String string = component.getString();

		for(char c : string.toCharArray())
		{
			boolean obfuscate = rng.nextBoolean();
			apex = apex.append(new StringTextComponent(String.valueOf(c)).withStyle(style -> style.setObfuscated(obfuscate)));
		}

		if(FantasyDice.loadComplete && apexNameComponent == null)
			apexNameComponent = apex;

		return apex;
	}
}
