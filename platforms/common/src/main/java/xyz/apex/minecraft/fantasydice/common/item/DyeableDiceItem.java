package xyz.apex.minecraft.fantasydice.common.item;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import xyz.apex.minecraft.fantasydice.common.util.DiceType;

public class DyeableDiceItem extends DiceItem implements DyeableLeatherItem
{
    public DyeableDiceItem(DiceType diceType, int sides, Properties properties)
    {
        super(diceType, sides, properties);
    }

    @Override
    public int getColor(ItemStack stack)
    {
        var stackTag = stack.getTag();
        return stackTag != null && stackTag.contains(DyeableLeatherItem.TAG_COLOR, Tag.TAG_ANY_NUMERIC) ? stackTag.getInt(DyeableLeatherItem.TAG_COLOR) : 0xFFFFFF;
    }
}
