package xyz.apex.forge.dicemod.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xyz.apex.forge.dicemod.init.Dice;
import xyz.apex.forge.dicemod.util.DiceHelper;

import javax.annotation.Nullable;
import java.util.List;

public class DiceItem extends Item
{
	public DiceItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		if(DiceHelper.throwDice(world, player, hand, 0))
			return ActionResult.sidedSuccess(player.getItemInHand(hand), world.isClientSide);
		if(!world.isClientSide)
			return ActionResult.pass(player.getItemInHand(hand));
		return ActionResult.fail(player.getItemInHand(hand));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced)
	{
		super.appendHoverText(stack, world, tooltip, advanced);

		// this method is used during early initialization
		// of mods, which means certain game elements (like tags)
		// will not have been initialized yet and would hard crash the game
		// world is only none null after this process
		if(world != null)
		{
			Dice dice = Dice.byItem(stack);
			tooltip.add(dice.createItemTooltipComponent(stack));
		}
	}
}
