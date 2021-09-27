package xyz.apex.forge.dicemod.item;

import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import xyz.apex.forge.dicemod.init.DStrings;

public class DyeableDiceItem extends DiceItem implements IDyeableArmorItem
{
	public DyeableDiceItem(Properties properties)
	{
		super(properties);
	}

	// allows stacks to override the default color
	protected int getDefaultColor(ItemStack stack)
	{
		CompoundNBT displayTag = stack.getTagElement(DStrings.NBT_DISPLAY);
		return displayTag != null && displayTag.contains(DStrings.NBT_COLOR_DEFAULT, Constants.NBT.TAG_ANY_NUMERIC) ? displayTag.getInt(DStrings.NBT_COLOR_DEFAULT) : getDefaultColor();
	}

	protected int getDefaultColor()
	{
		// default color is white / greyscale
		return 16777215;
	}

	@Override
	public int getColor(ItemStack stack)
	{
		CompoundNBT displayTag = stack.getTagElement(DStrings.NBT_DISPLAY);
		// exactly the same as default implementation from IDyeableArmorItem
		// just changed the default color from leather armor brown `10511680`
		// to be generic white / greyscale
		return displayTag != null && displayTag.contains(DStrings.NBT_COLOR, Constants.NBT.TAG_ANY_NUMERIC) ? displayTag.getInt(DStrings.NBT_COLOR) : getDefaultColor(stack);
	}
}
