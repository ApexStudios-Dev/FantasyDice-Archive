package xyz.apex.forge.fantasydice.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.util.DiceHelper;

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
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var cooldown = FantasyDice.CONFIG.diceCooldown.get();

		if(cooldown > 0)
			player.getCooldowns().addCooldown(this, cooldown);

		if(flip(level, player, hand, stack))
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(new TranslatableComponent(FantasyDice.COIN_DESC));
	}

	public static boolean flip(Level level, Player player, InteractionHand hand, ItemStack stack)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		var rng = level.random;
		var heads = 0;
		var tails = 0;

		for(var i = 0; i < stack.getCount(); i++)
		{
			var roll = DiceHelper.roll(rng, 1, 2, 0);

			if(roll == HEADS)
				heads++;
			else if(roll == TAILS)
				tails++;
			else
				throw new RuntimeException("Invalid coin flip state");
		}

		var message = buildFlipMessage(player, stack, heads, tails);
		DiceHelper.sendMessageToPlayers(player, message);
		return true;
	}

	public static MutableComponent buildFlipMessage(Player player, ItemStack stack, int heads, int tails)
	{
		// <player> flipped <N_heads> Heads & <N_tails> Tails
		return new TranslatableComponent(
				FantasyDice.COIN_FLIP,
				player.getDisplayName(),
				new TextComponent(String.valueOf(heads)).withStyle(style -> style.withItalic(true)),
				new TextComponent(String.valueOf(tails)).withStyle(style -> style.withItalic(true))
		).withStyle(style -> style
				.withHoverEvent(new HoverEvent(
						HoverEvent.Action.SHOW_ITEM,
						new HoverEvent.ItemStackInfo(stack)
				))
		);
	}
}
