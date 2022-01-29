package xyz.apex.forge.fantasydice.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.init.DiceType;
import xyz.apex.forge.fantasydice.init.FTDiceTypes;
import xyz.apex.forge.fantasydice.util.DiceHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class DiceItem extends Item
{
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
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getItemInHand(hand);

		if(DiceHelper.throwDice(level, player, hand, stack, 0))
			return ActionResult.sidedSuccess(stack, level.isClientSide);
		return ActionResult.pass(stack);
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

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return diceType != null && diceType.usesFoil() || super.isFoil(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		if(diceType != null)
		{
			tooltip.add(new TranslationTextComponent(FantasyDice.DIE_ROLL_DESC_KEY, 1, sides)
					.withStyle(style -> diceType
							.withStyle(stack, style)
					)
			);
		}
	}

	private IFormattableTextComponent buildNameComponent(ItemStack stack)
	{
		if(diceType == FTDiceTypes.DICE_APEX)
			return buildApexNameComponent(stack);
		return new TranslationTextComponent(getDescriptionId()).withStyle(style -> withStyle(stack, style));
	}

	private IFormattableTextComponent buildApexNameComponent(ItemStack stack)
	{
		return new TranslationTextComponent(
				FantasyDice.DIE_APEX_NAME,
				new StringTextComponent("NULL").withStyle(style -> withStyle(stack, style).setObfuscated(true)),
				sides
		).withStyle(style -> withStyle(stack, style));
	}

	private Style withStyle(ItemStack stack, Style style)
	{
		return diceType == null ? style : diceType.withStyle(stack, style);
	}
}
