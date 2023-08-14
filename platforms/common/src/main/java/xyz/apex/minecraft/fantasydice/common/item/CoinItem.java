package xyz.apex.minecraft.fantasydice.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xyz.apex.minecraft.apexcore.common.lib.helper.InteractionResultHelper;
import xyz.apex.minecraft.fantasydice.common.Coins;

public final class CoinItem extends Item
{
    private final TextColor color;

    public CoinItem(TextColor color, Properties properties)
    {
        super(properties);

        this.color = color;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        var stack = player.getItemInHand(hand);

        if(hand != InteractionHand.MAIN_HAND)
            return InteractionResultHelper.ItemUse.denyUsage(stack);

        var cooldown = player.getCooldowns();

        if(cooldown.isOnCooldown(this))
            return InteractionResultHelper.ItemUse.noActionTaken(stack);

        if(!level.isClientSide)
        {
            var heads = 0;
            var tails = 0;

            for(var i = 0; i < stack.getCount(); i++)
            {
                if(level.random.nextBoolean())
                    heads++;
                else
                    tails++;
            }

            player.sendSystemMessage(buildChatMessage(player, stack, heads, tails));
        }

        cooldown.addCooldown(this, 20);
        return InteractionResultHelper.ItemUse.succeedAndSwingArmBothSides(stack, level.isClientSide);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return Component.translatable(getDescriptionId(stack)).withStyle(style -> style.withColor(color));
    }

    private Component buildChatMessage(Player roller, ItemStack dice, int heads, int tails)
    {
        return Component.translatable(
                Coins.TXT_FLIPPED,
                roller.getDisplayName(),
                Component.translatable(
                        Coins.TXT_FLIPPED_COUNT, heads, tails)
                         .withStyle(style -> style.withColor(color))
        ).withStyle(style -> style
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        dice.getHoverName().plainCopy().withStyle(style1 -> style1.withColor(color))
                ))
        );
    }
}
