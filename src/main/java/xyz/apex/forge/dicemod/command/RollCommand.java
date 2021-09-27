package xyz.apex.forge.dicemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.data.LanguageGenerator;
import xyz.apex.forge.dicemod.util.DiceHelper;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class RollCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
				literal("roll")
						.then(
								argument("max", integer(1))
										.executes(ctx -> {
											ServerPlayerEntity thrower = ctx.getSource().getPlayerOrException();
											int max = getInteger(ctx, "max");
											int roll = DiceHelper.roll(thrower.getRandom(), 1, max);

											TextComponent component = new TranslationTextComponent(
													LanguageGenerator.DICE_ROLL_KEY,
													thrower.getDisplayName(),
													new StringTextComponent(roll + " (d" + max + ")").withStyle(style -> style.withItalic(true))
											);

											DiceMod.sendMessageToPlayers(thrower, component);
											return 1;
										})
						)

		);
	}
}
