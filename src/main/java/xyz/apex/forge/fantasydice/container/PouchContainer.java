package xyz.apex.forge.fantasydice.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;

import xyz.apex.forge.apexcore.lib.container.ItemInventoryContainer;
import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.container.slot.DiceSlot;

import javax.annotation.Nullable;

public final class PouchContainer extends ItemInventoryContainer
{
	public PouchContainer(@Nullable ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, ItemInventory itemInventory)
	{
		super(containerType, windowId, playerInventory, itemInventory);
	}

	@Override
	protected void addSlots()
	{
		for(int row = 0; row < 3; row++)
		{
			for(int col = 0; col < 6; col++)
			{
				addSlot(new DiceSlot(itemInventory, opener, col + row * 6, 35 + col * 18, 17 + row * 18));
			}
		}
	}
}
