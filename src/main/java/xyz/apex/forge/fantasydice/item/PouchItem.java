package xyz.apex.forge.fantasydice.item;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.apexcore.lib.item.InventoryItem;
import xyz.apex.forge.fantasydice.container.PouchMenu;
import xyz.apex.forge.fantasydice.init.FTMenus;

public class PouchItem extends InventoryItem<PouchMenu> implements DyeableLeatherItem
{
	public PouchItem(Properties properties)
	{
		super(properties);
	}

	@Override
	protected MenuType<PouchMenu> getContainerType()
	{
		return FTMenus.POUCH.asMenuType();
	}

	@Override
	protected ItemInventory createInventory(ItemStack stack)
	{
		return new ItemInventory(stack, 18);
	}

	@Override
	protected PouchMenu createContainer(MenuType<PouchMenu> containerType, int i, Inventory playerInventory, ItemInventory itemInventory)
	{
		return FTMenus.POUCH.create(i, playerInventory);
	}
}
