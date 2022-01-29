package xyz.apex.forge.fantasydice.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.apexcore.lib.container.slot.ItemInventorySlot;
import xyz.apex.forge.fantasydice.init.FTTags;

public final class DiceSlot extends ItemInventorySlot
{
	public DiceSlot(ItemInventory itemInventory, PlayerEntity opener, int slotIndex, int slotX, int slotY, boolean allowOtherPlayerInteraction)
	{
		super(itemInventory, opener, slotIndex, slotX, slotY, allowOtherPlayerInteraction);
	}

	public DiceSlot(ItemInventory itemInventory, PlayerEntity opener, int slotIndex, int slotX, int slotY)
	{
		this(itemInventory, opener, slotIndex, slotX, slotY, false);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		if(!stack.getItem().is(FTTags.Items.DICE))
			return false;
		return super.mayPlace(stack);
	}
}
