package xyz.apex.minecraft.fantasydice.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.apex.minecraft.fantasydice.common.item.DiceItem;

public final class DicePouchContainer extends SimpleContainer
{
    public static final int SLOT_COUNT = 18;
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_SLOT = "Slot";

    private final ItemStack stack;
    private boolean dirty = false;

    public DicePouchContainer(ItemStack stack)
    {
        super(SLOT_COUNT);

        this.stack = stack;

        var stackTag = stack.getTag();

        if(stackTag != null && stackTag.contains(NBT_ITEMS, Tag.TAG_LIST))
            fromTag(stackTag.getList(NBT_ITEMS, Tag.TAG_COMPOUND));
    }

    @Override
    public void fromTag(ListTag itemsTag)
    {
        clearContent();

        for(var i = 0; i < itemsTag.size(); i++)
        {
            var itemTag = itemsTag.getCompound(i);
            var slotIndex = itemTag.getByte(NBT_SLOT) & 0xFF;

            if(slotIndex >= SLOT_COUNT)
                continue;

            setItem(slotIndex, ItemStack.of(itemTag));
        }

        dirty = false;
    }

    @Override
    public ListTag createTag()
    {
        var itemsTag = new ListTag();

        for(var i = 0; i < SLOT_COUNT; i++)
        {
            var item = getItem(i);

            if(item.isEmpty())
                continue;

            var itemTag = item.save(new CompoundTag());
            itemTag.putByte(NBT_SLOT, (byte) i);
            itemsTag.add(itemTag);
        }

        return itemsTag;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        var item = stack.getItem();
        return item.canFitInsideContainerItems() && item instanceof DiceItem;
    }

    @Override
    public void setChanged()
    {
        dirty = true;
        super.setChanged();
    }

    @Override
    public void stopOpen(Player player)
    {
        if(!dirty)
            return;

        var itemsTag = createTag();

        if(itemsTag.isEmpty())
            stack.removeTagKey(NBT_ITEMS);
        else
            stack.addTagElement(NBT_ITEMS, itemsTag);

        dirty = false;
    }

    public static NonNullList<ItemStack> getItems(ItemStack stack)
    {
        var stackTag = stack.getTag();
        var items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

        if(stackTag == null || !stackTag.contains(NBT_ITEMS))
            return items;

        ContainerHelper.loadAllItems(stackTag, items);
        return items;
    }
}
