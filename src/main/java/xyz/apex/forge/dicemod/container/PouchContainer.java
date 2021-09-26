package xyz.apex.forge.dicemod.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.item.PouchItem;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PouchContainer extends Container
{
	public static final int ROWS = 3;
	public static final int COLS = 6;
	public static final int SLOTS = COLS * ROWS;

	private final PlayerEntity player;
	private final PouchItem.Inv pouchInventory;

	public PouchContainer(@Nullable ContainerType<?> containerType, PlayerInventory playerInventory, PouchItem.Inv pouchInventory, int windowId)
	{
		super(containerType, windowId);

		this.pouchInventory = pouchInventory;
		player = playerInventory.player;

		// checkContainerSize(pouchInventory, SLOTS);
		bindPlayerInventory(this, playerInventory, this::addSlot);
		pouchInventory.startOpen(player);

		// pouch slots
		for(int row = 0; row < ROWS; row++)
		{
			for(int col = 0; col < COLS; col++)
			{
				addSlot(new DiceSlot(pouchInventory, col + row * 6, 35 + col * 18, 17 + row * 18));
			}
		}
	}

	@Deprecated // internal use only
	public PouchContainer(int windowId, PlayerInventory inv, PacketBuffer data)
	{
		this(DiceMod.POUCH_CONTAINER.get(), inv, new PouchItem.Inv(inv.player.getItemInHand(data.readEnum(Hand.class))), windowId);
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return player.getUUID().equals(this.player.getUUID());
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int slotIndex)
	{
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();
			result = slotStack.copy();

			if(slotIndex < SLOTS)
			{
				if(!moveItemStackTo(slotStack, SLOTS, slots.size(), true))
					return ItemStack.EMPTY;
			}
			else if(!moveItemStackTo(slotStack, 0, SLOTS, false))
				return ItemStack.EMPTY;

			if(slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return result;
	}

	@Override
	public void removed(PlayerEntity player)
	{
		super.removed(player);
		pouchInventory.stopOpen(player);
	}

	// Consumer<Slot> -> Container#addSlot
	// why must it be protected
	public static void bindPlayerInventory(Container container, PlayerInventory playerInventory, Consumer<Slot> addSlot)
	{
		// main inventory
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlot.accept(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// hotbar
		for(int k = 0; k < 9; k++)
		{
			addSlot.accept(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}
}
