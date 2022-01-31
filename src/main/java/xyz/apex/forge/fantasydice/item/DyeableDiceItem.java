package xyz.apex.forge.fantasydice.item;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

import xyz.apex.forge.apexcore.lib.constants.NbtNames;

public class DyeableDiceItem extends DiceItem implements DyeableLeatherItem
{
	public DyeableDiceItem(Properties properties, int sides)
	{
		super(properties, sides);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		// exactly the same as vanilla code in IDyeableArmorItem
		// only change is to make the default color white / greyscale (16777215)
		var displayTag = stack.getTagElement(NbtNames.DISPLAY);
		return displayTag != null && displayTag.contains(NbtNames.COLOR, Tag.TAG_COMPOUND) ? displayTag.getInt(NbtNames.COLOR) : 16777215;
	}
}
