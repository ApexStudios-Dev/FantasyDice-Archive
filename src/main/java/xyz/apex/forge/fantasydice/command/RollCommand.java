package xyz.apex.forge.fantasydice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.util.DiceHelper;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class RollCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("roll")
				.then(argument("max", integer(1, 999999999))
						.executes(ctx -> {
							ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
							int max = IntegerArgumentType.getInteger(ctx, "max");
							int roll = DiceHelper.roll(player.getRandom(), 1, max);

							DiceHelper.sendMessageToPlayers(
									player,
									new TranslationTextComponent(
											FantasyDice.DIE_ROLL_KEY,
											player.getDisplayName(),
											new TranslationTextComponent(FantasyDice.DIE_ROLL_RESULT_KEY, roll, "", max)
									)
							);
							return 1;
						})
				)
		);
	}
}
