package xyz.apex.forge.dicemod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.apex.forge.dicemod.util.DiceHelper;

public final class DiceItem extends Item
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
}
