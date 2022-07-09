package xyz.apex.forge.fantasydice.container.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemInventory extends SimpleContainer
{
	public static final String NBT_INVENTORY = "Inventory";
	public static final String NBT_ITEMS = "Items";
	public static final String NBT_SLOTS = "Slots";

	private final ItemStack item;
	private int openCount = 0;

	public ItemInventory(ItemStack item, int slots)
	{
		super(slots);

		this.item = item;
	}

	@Override
	public void startOpen(Player player)
	{
		if(openCount == 0)
		{
			var invTag = item.getTagElement(NBT_INVENTORY);

			if(invTag != null)
			{
				var slotTag = invTag.getList(NBT_ITEMS, Tag.TAG_COMPOUND);

				for(var i = 0; i < slotTag.size(); i++)
				{
					var itemTag = slotTag.getCompound(i);
					var slotIndex = itemTag.getByte(NBT_SLOTS) & 255;

					if(slotIndex < getContainerSize())
						setItem(slotIndex, ItemStack.of(itemTag));
				}
			}
		}

		openCount++;
	}

	@Override
	public void stopOpen(Player player)
	{
		openCount--;

		if(openCount == 0)
		{
			var invTag = new CompoundTag();
			var slotTag = new ListTag();

			for(var i = 0; i < getContainerSize(); i++)
			{
				var stack = getItem(i);

				if(!stack.isEmpty())
				{
					var itemTag = new CompoundTag();
					itemTag.putByte(NBT_SLOTS, (byte) i);
					stack.save(itemTag);
					slotTag.add(itemTag);
				}
			}

			invTag.put(NBT_ITEMS, slotTag);
			item.addTagElement(NBT_INVENTORY, invTag);
		}
	}

	public ItemStack getContainerItem()
	{
		return item;
	}
}