package xyz.apex.forge.fantasytable.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xyz.apex.forge.fantasytable.util.DiceHelper;

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
		ItemStack die = player.getItemInHand(hand);
		int sides = DiceHelper.getSides(die);

		if(DiceHelper.throwDice(world, player, hand, die, 0, sides))
			return ActionResult.sidedSuccess(die, world.isClientSide);
		if(!world.isClientSide)
			return ActionResult.pass(die);
		return ActionResult.fail(die);
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
			int sides = DiceHelper.getSides(stack);
			tooltip.add(DiceHelper.createItemTooltipComponent(stack, 1, sides));
		}
	}
}
