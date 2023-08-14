package xyz.apex.minecraft.fantasydice.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.apexcore.common.lib.PhysicalSide;
import xyz.apex.minecraft.apexcore.common.lib.PlatformOnly;
import xyz.apex.minecraft.apexcore.common.lib.helper.InteractionResultHelper;
import xyz.apex.minecraft.apexcore.common.lib.hook.MenuHooks;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.menu.DicePouchMenu;
import xyz.apex.minecraft.fantasydice.common.util.DicePouchContainer;

import java.util.List;
import java.util.Optional;

public class DicePouchItem extends Item implements DyeableLeatherItem
{
    public DicePouchItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        var stack = player.getItemInHand(hand);

        if(!level.isClientSide)
            MenuHooks.get().openMenu(player, stack.getHoverName(), (syncId, inventory, opener) -> DicePouchMenu.create(syncId, inventory, hand), buffer -> buffer.writeEnum(hand));

        return InteractionResultHelper.ItemUse.succeedAndSwingArmBothSides(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag isAdvanced)
    {
        if(!shouldShowMoreContents(true, stack))
            return;

        tooltips.add(FantasyDice.DICE_POUCH_TXT_SHIFT);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        return shouldShowMoreContents(false, stack) ? Optional.of(new BundleTooltip(DicePouchContainer.getItems(stack), 0)) : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity entity)
    {
        ItemUtils.onContainerDestroyed(entity, DicePouchContainer.getItems(entity.getItem()).stream());
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    @PlatformOnly(PlatformOnly.FORGE) // TODO: Does fabric have anything similar to this?
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    private static boolean shouldShowMoreContents(boolean text, ItemStack stack)
    {
        return PhysicalSide.CLIENT.callWhenOn(() -> () -> {
            var client = Minecraft.getInstance();

            if(client.player != null && client.player.containerMenu instanceof DicePouchMenu menu && menu.isContainerStack(stack))
                return false;

            return text != Screen.hasShiftDown();
        }).orElse(false);
    }
}
