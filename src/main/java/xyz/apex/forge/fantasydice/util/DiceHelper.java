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

	public static int roll(Random rng, int min, int max)
	{
		if(max < min)
		{
			int tmp = max;
			max = min;
			min = tmp;
		}

		min = Math.max(min, 0);
		max = Math.max(max, 1);

		/*if(loaded)
			min = Math.max(1, max / 2);*/

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

	public static boolean throwDice(World level, PlayerEntity player, Hand hand, ItemStack stack, int min)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		DiceItem die = (DiceItem) stack.getItem();
		int sides = die.getSides();
		int stackCount = stack.getCount();
		int maxPossibleRoll = sides * stackCount;
		int[] rolls = IntStream.range(0, stackCount).map(i -> roll(level.random, min, sides)).toArray();
		DiceType<?, ?> diceType = die.getDiceType();
		rolls = diceType.onRoll(player, hand, stack, min, sides, rolls);
		int roll = Arrays.stream(rolls).sum();
		roll += diceType.getDiceQuality();

		if(!diceType.matches(FTDiceTypes.DICE_APEX)) // apex goes negative, clamping will break it
			roll = MathHelper.clamp(roll, min, maxPossibleRoll);

		IFormattableTextComponent textComponent = createTextComponent(player, stack, die, roll, sides);
		sendMessageToPlayers(player, textComponent);

		return true;
	}

	private static IFormattableTextComponent createTextComponent(PlayerEntity player, ItemStack stack, DiceItem die, int roll, int sides)
	{
		DiceType<?, ?> diceType = die.getDiceType();

		return new TranslationTextComponent(
				FantasyDice.DIE_ROLL_KEY,
				player.getDisplayName(),
				new TranslationTextComponent(FantasyDice.DIE_ROLL_RESULT_KEY, roll, stack.getCount(), sides).withStyle(style -> diceType.withStyle(stack, style))
		).withStyle(style -> style
				.withHoverEvent(
						new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								stack.getHoverName().copy().withStyle(hoverStyle -> diceType.withStyle(stack, hoverStyle))
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
