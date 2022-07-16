package xyz.apex.forge.fantasydice.container;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import xyz.apex.forge.apexcore.lib.container.BaseMenu;
import xyz.apex.forge.fantasydice.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.init.FTItems;
import xyz.apex.forge.fantasydice.init.FTTags;

public final class PouchContainer extends BaseMenu
{
	private final InteractionHand hand;
	private final ItemStack stack;
	private final IItemHandler itemHandler;
	private final ItemInventory inventory;

	public PouchContainer(@Nullable MenuType<? extends PouchContainer> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf buffer)
	{
		super(containerType, windowId, playerInventory, buffer);

		hand = buffer.readEnum(InteractionHand.class);
		stack = playerInventory.player.getItemInHand(hand);
		inventory = new ItemInventory(stack, 18);
		inventory.startOpen(player);
		itemHandler = new InvWrapper(inventory);

		bindItemHandlerSlots(this, itemHandler, 3, 6, 35, 17, DiceSlot::new);
		bindPlayerInventory(this, PouchSlot::new);
	}

	@Override
	public boolean stillValid(Player player)
	{
		return super.stillValid(player) && inventory.stillValid(player);
	}

	@Override
	protected IItemHandler getItemHandler()
	{
		return itemHandler;
	}

	@Override
	protected void onInventoryChanges()
	{
		inventory.setChanged();
		// ItemInventory.save(inventory);
	}

	@Override
	public void removed(Player player)
	{
		inventory.setChanged();
		super.removed(player);
		inventory.stopOpen(this.player);
	}

	public static final class DiceSlot extends SlotItemHandler
	{
		public DiceSlot(IItemHandler itemHandler, int slotIndex, int x, int y)
		{
			super(itemHandler, slotIndex, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return stack.is(FTTags.Items.DICE);
		}
	}

	public static final class PouchSlot extends Slot
	{
		public PouchSlot(Container container, int slotIndex, int x, int y)
		{
			super(container, slotIndex, x, y);
		}

		@Override
		public boolean mayPickup(Player player)
		{
			return !FTItems.POUCH.isIn(getItem());
		}
	}
}