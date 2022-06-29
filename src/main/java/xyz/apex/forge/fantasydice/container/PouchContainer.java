package xyz.apex.forge.fantasydice.container;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import xyz.apex.forge.apexcore.lib.container.ItemInventoryContainer;
import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.container.slot.DiceSlot;

public final class PouchContainer extends ItemInventoryContainer
{
	public PouchContainer(@Nullable MenuType<?> containerType, int windowId, Inventory playerInventory, ItemInventory itemInventory)
	{
		super(containerType, windowId, playerInventory, itemInventory);
	}

	@Override
	protected void addSlots()
	{
		for(var row = 0; row < 3; row++)
		{
			for(var col = 0; col < 6; col++)
			{
				addSlot(new DiceSlot(itemInventory, opener, col + row * 6, 35 + col * 18, 17 + row * 18));
			}
		}
	}
}