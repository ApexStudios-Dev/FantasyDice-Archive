package xyz.apex.minecraft.fantasydice.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import xyz.apex.minecraft.apexcore.common.lib.menu.EnhancedSlot;
import xyz.apex.minecraft.apexcore.common.lib.menu.SimpleContainerMenu;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.util.DicePouchContainer;

public final class DicePouchMenu extends SimpleContainerMenu
{
    private final ItemStack stack;

    private DicePouchMenu(MenuType<? extends DicePouchMenu> menuType, int syncId, Inventory inventory, ItemStack stack)
    {
        super(menuType, syncId, inventory, new DicePouchContainer(stack));

        this.stack = stack;
    }

    public boolean isContainerStack(ItemStack stack)
    {
        return ItemStack.matches(this.stack, stack);
    }

    @Override
    protected void bindSlots(Inventory playerInventory)
    {
        bindContainer(container, 3, 6, 35, 17, this::addSlot);

        // player inventory
        for(var i = 0; i < 3; i++)
        {
            for(var j = 0; j < 9; j++)
            {
                addSlot(new DicePouchSlot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(var i = 0; i < 9; i++)
        {
            addSlot(new DicePouchSlot(playerInventory, i, 8 + i * 18, 84 + 58));
        }
    }

    public static DicePouchMenu create(int syncId, Inventory inventory, InteractionHand hand)
    {
        var stack = inventory.player.getItemInHand(hand);
        Validate.isTrue(FantasyDice.DICE_POUCH_ITEM.is(stack));
        return new DicePouchMenu(FantasyDice.DICE_POUCH_MENU.value(), syncId, inventory, stack);
    }

    public static DicePouchMenu forNetwork(MenuType<? extends DicePouchMenu> menuType, int syncId, Inventory inventory, FriendlyByteBuf buffer)
    {
        var hand = buffer.readEnum(InteractionHand.class);
        return create(syncId, inventory, hand);
    }

    private final class DicePouchSlot extends EnhancedSlot
    {
        private DicePouchSlot(Inventory inventory, int slotIndex, int x, int y)
        {
            super(inventory, slotIndex, x, y);
        }

        @Override
        public boolean mayPickup(Player player)
        {
            return !isContainerStack(getItem());
        }
    }
}
