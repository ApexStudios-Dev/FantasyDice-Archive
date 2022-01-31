package xyz.apex.forge.fantasydice.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemContainer;
import xyz.apex.forge.apexcore.lib.container.slot.ItemInventorySlot;
import xyz.apex.forge.fantasydice.init.FTTags;

public final class DiceSlot extends ItemInventorySlot
{
	public DiceSlot(ItemContainer itemContainer, Player opener, int slotIndex, int slotX, int slotY, boolean allowOtherPlayerInteraction)
	{
		super(itemContainer, opener, slotIndex, slotX, slotY, allowOtherPlayerInteraction);
	}

	public DiceSlot(ItemContainer itemContainer, Player opener, int slotIndex, int slotX, int slotY)
	{
		this(itemContainer, opener, slotIndex, slotX, slotY, false);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		if(!stack.is(FTTags.Items.DICE))
			return false;
		return super.mayPlace(stack);
	}
}
