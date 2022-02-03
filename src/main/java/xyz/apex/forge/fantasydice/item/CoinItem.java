package xyz.apex.forge.fantasydice.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.util.DiceHelper;

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

		if(flip(level, player, hand, stack))
			return ActionResult.sidedSuccess(stack, level.isClientSide);
		return ActionResult.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TranslationTextComponent(FantasyDice.COIN_DESC));
	}

	public static boolean flip(World level, PlayerEntity player, Hand hand, ItemStack stack)
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

		IFormattableTextComponent message = buildFlipMessage(player, stack, heads, tails);
		DiceHelper.sendMessageToPlayers(player, message);
		return true;
	}

	public static IFormattableTextComponent buildFlipMessage(PlayerEntity player, ItemStack stack, int heads, int tails)
	{
		// <player> flipped <N_heads> Heads & <N_tails> Tails
		return new TranslationTextComponent(
				FantasyDice.COIN_FLIP,
				player.getDisplayName(),
				heads,
				tails
		).withStyle(style -> style
				.withHoverEvent(new HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						stack.getHoverName()
				))
		);
	}
}
