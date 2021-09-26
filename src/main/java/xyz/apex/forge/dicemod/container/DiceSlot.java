package xyz.apex.forge.dicemod.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import xyz.apex.forge.dicemod.DiceMod;

public class DiceSlot extends Slot
{
	public DiceSlot(IInventory inventory, int slotIndex, int xPos, int yPos)
	{
		super(inventory, slotIndex, xPos, yPos);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.getItem().is(DiceMod.DICE);
	}
}
