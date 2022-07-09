package xyz.apex.forge.fantasydice.container;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import xyz.apex.forge.apexcore.revamp.container.BaseMenu;
import xyz.apex.forge.fantasydice.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.init.FTTags;

public final class PouchContainer extends BaseMenu
{
	public PouchContainer(@Nullable MenuType<? extends PouchContainer> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf buffer, ItemInventory inventory)
	{
		super(containerType, windowId, playerInventory, buffer);

		bindPlayerInventory(this);
		bindItemHandlerSlots(this, new InvWrapper(inventory), 3, 6, 35, 17, DiceSlot::new);
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
}