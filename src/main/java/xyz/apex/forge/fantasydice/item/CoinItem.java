package xyz.apex.forge.fantasydice.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.init.FTItems;
import xyz.apex.forge.fantasydice.util.DiceHelper;
import xyz.apex.java.utility.nullness.NonnullUnaryOperator;

import javax.annotation.Nullable;
import java.util.List;

public class CoinItem extends Item
{
	public static final int HEADS = 1;
	public static final int TAILS = 2;

	public CoinItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		int cooldown = FantasyDice.CONFIG.diceCooldown.get();

		if(cooldown > 0)
			player.getCooldowns().addCooldown(this, cooldown);

		if(flip(level, player, hand, stack, style -> withStyle(stack, style)))
			return ActionResult.sidedSuccess(stack, level.isClientSide);
		return ActionResult.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TranslationTextComponent(FantasyDice.COIN_DESC).withStyle(style -> withStyle(stack, style)));
	}

	@Override
	public ITextComponent getDescription()
	{
		return buildNameComponent(ItemStack.EMPTY);
	}

	@Override
	public ITextComponent getName(ItemStack stack)
	{
		return buildNameComponent(stack);
	}

	private IFormattableTextComponent buildNameComponent(ItemStack stack)
	{
		return new TranslationTextComponent(getDescriptionId()).withStyle(style -> withStyle(stack, style));
	}

	private Style withStyle(ItemStack stack, Style style)
	{
		if(FTItems.IRON_COIN.is(this))
			return FTDiceTypes.DICE_IRON.withStyle(stack, style);
		if(FTItems.GOLDEN_COIN.is(this))
			return FTDiceTypes.DICE_GOLD.withStyle(stack, style);
		return style;
	}

	public static boolean flip(World level, PlayerEntity player, Hand hand, ItemStack stack, NonnullUnaryOperator<Style> withStyle)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		int heads = 0;
		int tails = 0;

		for(int i = 0; i < stack.getCount(); i++)
		{
			int roll = DiceHelper.roll(level.random, 1, 2, 0);

			if(roll == HEADS)
				heads++;
			else if(roll == TAILS)
				tails++;
			else
				throw new RuntimeException("Invalid coin flip state");
		}

		IFormattableTextComponent message = buildFlipMessage(player, stack, heads, tails, withStyle);
		DiceHelper.sendMessageToPlayers(player, message);
		return true;
	}

	public static IFormattableTextComponent buildFlipMessage(PlayerEntity player, ItemStack stack, int heads, int tails, NonnullUnaryOperator<Style> withStyle)
	{
		// prefix: <player> flipped
		// suffix: <N_heads> Heads & <N_tails> Tails
		// full: <player> flipped <N_heads> Heads & <N_tails> Tails
		IFormattableTextComponent prefix = new TranslationTextComponent(
				FantasyDice.COIN_FLIP_PREFIX,
				player.getDisplayName()
		);

		IFormattableTextComponent suffix = new TranslationTextComponent(FantasyDice.COIN_FLIP_SUFFIX, heads, tails)
				.withStyle(style -> withStyle
						.apply(style)
						.withHoverEvent(new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								stack.getHoverName()
						))
				);

		return prefix.append(" ").append(suffix);
	}
}
