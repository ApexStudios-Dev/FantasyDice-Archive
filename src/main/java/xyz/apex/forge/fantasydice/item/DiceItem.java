package xyz.apex.forge.fantasydice.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.DiceType;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.util.DiceHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DiceItem extends Item
{
	public static final Random RNG = new Random();

	private final int sides;
	@Nullable private DiceType<?, ?> diceType;

	public DiceItem(Properties properties, int sides)
	{
		super(properties);

		this.sides = sides;
	}

	public DiceType<?, ?> getDiceType()
	{
		return Objects.requireNonNull(diceType);
	}

	// DO NOT CALL
	public void setDiceType(DiceType<?, ?> diceType)
	{
		if(this.diceType == null)
			this.diceType = diceType;
	}

	public int getSides()
	{
		return sides;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);

		if(DiceHelper.throwDice(level, player, hand, stack, 1))
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public Component getDescription()
	{
		return buildNameComponent(ItemStack.EMPTY);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		/*if(!stack.hasCustomHoverName())
			stack.setHoverName(buildNameComponent(stack));
		return stack.getHoverName();*/
		return buildNameComponent(stack);
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return diceType != null && diceType.usesFoil() || super.isFoil(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		if(diceType != null)
		{
			var min = 1;
			var max = sides;

			if(diceType.matches(FTDiceTypes.DICE_APEX))
			{
				min = -1;
				max *= -1;
			}

			tooltip.add(new TranslatableComponent(FantasyDice.DIE_ROLL_DESC_KEY, min, max)
					.withStyle(style -> diceType
							.withStyle(stack, style)
					)
			);

			tooltip.add(diceType.getType().getComponent(stack, diceType));
		}
	}

	private MutableComponent buildNameComponent(ItemStack stack)
	{
		MutableComponent nameComponent = new TranslatableComponent(getDescriptionId());

		if(diceType != null && diceType.matches(FTDiceTypes.DICE_APEX))
			nameComponent = DiceHelper.makeApexComponent(RNG, nameComponent);
		return nameComponent.withStyle(style -> withStyle(stack, style));
	}

	private Style withStyle(ItemStack stack, Style style)
	{
		return diceType == null ? style : diceType.withStyle(stack, style);
	}
}
