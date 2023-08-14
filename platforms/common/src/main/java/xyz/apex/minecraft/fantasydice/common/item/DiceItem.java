package xyz.apex.minecraft.fantasydice.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xyz.apex.minecraft.apexcore.common.lib.helper.InteractionResultHelper;
import xyz.apex.minecraft.fantasydice.common.DiceTypes;
import xyz.apex.minecraft.fantasydice.common.util.DiceType;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiceItem extends Item
{
    private final DiceType diceType;
    private final int sides;

    public DiceItem(DiceType diceType, int sides, Properties properties)
    {
        super(properties);

        this.diceType = diceType;
        this.sides = sides;
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
            var rolls = new int[stack.getCount()];
            Arrays.setAll(rolls, i -> rollDie(player, stack));
            player.sendSystemMessage(buildChatMessage(player, stack, rolls));
        }

        cooldown.addCooldown(this, 20);
        return InteractionResultHelper.ItemUse.succeedAndSwingArmBothSides(stack, level.isClientSide);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return Component.translatable(getDescriptionId(stack)).withStyle(style -> style.withColor(diceType.getColor()));
    }

    private int rollDie(Player roller, ItemStack dice)
    {
        var originalRoll = roller.getRandom().nextIntBetweenInclusive(0, sides);
        var newRoll = diceType.modifyRoll(roller, dice, sides, originalRoll);

        var min = 1;
        var max = sides;

        if(diceType == DiceTypes.APEX)
        {
            min = sides * -1;
            max = -1;
        }

        return Mth.clamp(newRoll, min, max);
    }

    private Component buildChatMessage(Player roller, ItemStack dice, int[] rolls)
    {
        var color = diceType.getColor();
        var rollsComponent = new Component[] { Component.empty() };

        if(rolls.length > 1)
            rollsComponent[0] = Component.literal("\n").append(IntStream.of(rolls).mapToObj(Integer::toString).collect(Collectors.joining(", ", "[", "]"))).withStyle(style -> style.withItalic(true));

        return Component.translatable(
                DiceTypes.TXT_FLIPPED,
                roller.getDisplayName(),
                Component.translatable(
                        DiceTypes.TXT_FLIPPED_COUNT,
                        IntStream.of(rolls).sum(),
                        rolls.length,
                        sides
                ).withStyle(style -> style.withColor(color))
        ).withStyle(style -> style
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        dice.getHoverName()
                            .plainCopy()
                            .append(rollsComponent[0])
                            .withStyle(style1 -> style1.withColor(color))
                ))
        );
    }
}
