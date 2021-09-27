package xyz.apex.forge.fantasytable.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.DStrings;
import xyz.apex.forge.fantasytable.util.DiceHelper;

public final class RollCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		// formatter:off
		dispatcher.register(
				Commands.literal(DStrings.CMD_ROLL)
						.then(
								Commands.argument(DStrings.CMD_ARG_MAX, IntegerArgumentType.integer(1))
								        .executes(ctx -> {
											ServerPlayerEntity thrower = ctx.getSource().getPlayerOrException();
											int max = IntegerArgumentType.getInteger(ctx, DStrings.CMD_ARG_MAX);
											int roll = DiceHelper.roll(thrower.getRandom(), 1, max);

											TextComponent component = new TranslationTextComponent(
													FantasyTable.DICE_ROLL_KEY,
													thrower.getDisplayName(),
													// TODO: Should this component be localized?
													// `<dice_roll> (<dice_sides>)`
													new StringTextComponent(roll + " (" + max + ')').withStyle(style -> style.withItalic(true))
											);

											FantasyTable.sendMessageToPlayers(thrower, component);
											return 1;
										})
						)

		);
		// formatter:on
	}
}
