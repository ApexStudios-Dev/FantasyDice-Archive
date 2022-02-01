package xyz.apex.forge.fantasydice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.util.DiceHelper;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class RollCommand
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(literal("roll")
				.then(argument("max", integer(1, 999999999))
						.executes(ctx -> {
							var player = ctx.getSource().getPlayerOrException();
							var max = IntegerArgumentType.getInteger(ctx, "max");
							var roll = DiceHelper.roll(player.getRandom(), 1, max, 0);

							DiceHelper.sendMessageToPlayers(
									player,
									new TranslatableComponent(
											FantasyDice.DIE_ROLL_KEY,
											player.getDisplayName(),
											new TranslatableComponent(FantasyDice.DIE_ROLL_RESULT_KEY, roll, "", max)
									)
							);
							return 1;
						})
				)
		);
	}
}
