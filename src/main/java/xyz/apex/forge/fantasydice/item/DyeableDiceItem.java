package xyz.apex.forge.fantasydice.item;

import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class DyeableDiceItem extends DiceItem implements IDyeableArmorItem
{
	public static final String NBT_DISPLAY = "display";
	public static final String NBT_COLOR = "color";

	public DyeableDiceItem(Properties properties, int sides)
	{
		super(properties, sides);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		// exactly the same as vanilla code in IDyeableArmorItem
		// only change is to make the default color white / greyscale (16777215)
		CompoundNBT displayTag = stack.getTagElement(NBT_DISPLAY);
		return displayTag != null && displayTag.contains(NBT_COLOR, Constants.NBT.TAG_ANY_NUMERIC) ? displayTag.getInt(NBT_COLOR) : 16777215;
	}
}
