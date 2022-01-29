package xyz.apex.forge.fantasydice.item;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.apexcore.lib.item.InventoryItem;
import xyz.apex.forge.fantasydice.container.PouchContainer;
import xyz.apex.forge.fantasydice.init.FTContainers;

public class PouchItem extends InventoryItem<PouchContainer> implements IDyeableArmorItem
{
	public PouchItem(Properties properties)
	{
		super(properties);
	}

	@Override
	protected ContainerType<PouchContainer> getContainerType()
	{
		return FTContainers.POUCH.asContainerType();
	}

	@Override
	protected ItemInventory createInventory(ItemStack stack)
	{
		return new ItemInventory(stack, 18);
	}

	@Override
	protected PouchContainer createContainer(ContainerType<PouchContainer> containerType, int i, PlayerInventory playerInventory, ItemInventory itemInventory)
	{
		return FTContainers.POUCH.create(i, playerInventory);
	}
}
