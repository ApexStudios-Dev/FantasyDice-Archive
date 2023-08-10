package xyz.apex.forge.fantasydice.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.init.FTItems;
import xyz.apex.forge.fantasydice.util.DiceHelper;

import java.util.List;
import java.util.function.UnaryOperator;

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

		if(flip(level, player, hand, stack, style -> withStyle(stack, style)))
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(Component.translatable(FantasyDice.COIN_DESC).withStyle(style -> withStyle(stack, style).withItalic(true)));
	}

	@Override
	public Component getDescription()
	{
		return buildNameComponent(ItemStack.EMPTY);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		return buildNameComponent(stack);
	}

	private MutableComponent buildNameComponent(ItemStack stack)
	{
		return Component.translatable(getDescriptionId()).withStyle(style -> withStyle(stack, style));
	}

	private Style withStyle(ItemStack stack, Style style)
	{
		if(FTItems.IRON_COIN.is(this))
			return FTDiceTypes.DICE_IRON.withStyle(stack, style);
		if(FTItems.GOLDEN_COIN.is(this))
			return FTDiceTypes.DICE_GOLD.withStyle(stack, style);
		return style;
	}

	public static boolean flip(Level level, Player player, InteractionHand hand, ItemStack stack, UnaryOperator<Style> withStyle)
	{
		if(stack.isEmpty())
			return false;
		if(level.isClientSide)
			return true;

		var heads = 0;
		var tails = 0;

		for(var i = 0; i < stack.getCount(); i++)
		{
			var roll = DiceHelper.roll(level.random, 1, 2, 0, false);

			if(roll == HEADS)
				heads++;
			else if(roll == TAILS)
				tails++;
			else
				throw new RuntimeException("Invalid coin flip state");
		}

		var message = buildFlipMessage(player, stack, heads, tails, withStyle);
		DiceHelper.sendMessageToPlayers(player, message);
		return true;
	}

	public static MutableComponent buildFlipMessage(Player player, ItemStack stack, int heads, int tails, UnaryOperator<Style> withStyle)
	{
		// prefix: <player> flipped
		// suffix: <N_heads> Heads & <N_tails> Tails
		// full: <player> flipped <N_heads> Heads & <N_tails> Tails
		var prefix = Component.translatable(
				FantasyDice.COIN_FLIP_PREFIX,
				player.getDisplayName()
		);

		var suffix = Component.translatable(FantasyDice.COIN_FLIP_SUFFIX, heads, tails)
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
