package xyz.apex.forge.dicemod.item;

import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class DyeableDiceItem extends DiceItem implements IDyeableArmorItem
{
	public DyeableDiceItem(Properties properties)
	{
		super(properties);
	}

	// allows stacks to override the default color
	protected int getDefaultColor(ItemStack stack)
	{
		CompoundNBT displayTag = stack.getTagElement("display");
		return displayTag != null && displayTag.contains("color_default", Constants.NBT.TAG_ANY_NUMERIC) ? displayTag.getInt("color_default") : getDefaultColor();
	}

	protected int getDefaultColor()
	{
		// default color is white / greyscale
		return 16777215;
	}

	@Override
	public int getColor(ItemStack stack)
	{
		CompoundNBT displayTag = stack.getTagElement("display");
		// exactly the same as default implementation from IDyeableArmorItem
		// just changed the default color from leather armor brown `10511680`
		// to be generic white / greyscale
		return displayTag != null && displayTag.contains("color", Constants.NBT.TAG_ANY_NUMERIC) ? displayTag.getInt("color") : getDefaultColor(stack);
	}
}
