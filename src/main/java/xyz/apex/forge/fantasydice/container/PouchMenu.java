package xyz.apex.forge.fantasydice.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import xyz.apex.forge.apexcore.lib.container.ItemInventoryMenu;
import xyz.apex.forge.apexcore.lib.container.inventory.ItemContainer;
import xyz.apex.forge.fantasydice.container.slot.DiceSlot;

import javax.annotation.Nullable;

public final class PouchMenu extends ItemInventoryMenu
{
	public PouchMenu(@Nullable MenuType<?> menuType, int windowId, Inventory playerInventory, ItemContainer itemContainer)
	{
		super(menuType, windowId, playerInventory, itemContainer);
	}

	@Override
	protected void addSlots()
	{
		for(var row = 0; row < 3; row++)
		{
			for(var col = 0; col < 6; col++)
			{
				addSlot(new DiceSlot(itemContainer, opener, col + row * 6, 35 + col * 18, 17 + row * 18));
			}
		}
	}
}
