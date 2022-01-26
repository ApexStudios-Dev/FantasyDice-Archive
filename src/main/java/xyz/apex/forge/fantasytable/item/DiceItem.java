package xyz.apex.forge.fantasytable.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import xyz.apex.forge.fantasytable.init.DiceType;
import xyz.apex.forge.fantasytable.util.DiceHelper;

import javax.annotation.Nullable;
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
		return buildNameComponent();
	}

	@Override
	public ITextComponent getName(ItemStack stack)
	{
		return buildNameComponent();
	}

	private IFormattableTextComponent buildNameComponent()
	{
		IFormattableTextComponent nameComponent = new TranslationTextComponent(getDescriptionId());

		if(diceType == null)
			return nameComponent;

		return nameComponent.withStyle(diceType::withNameStyle);
	}
}
