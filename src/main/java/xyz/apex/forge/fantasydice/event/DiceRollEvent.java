package xyz.apex.forge.fantasydice.event;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import xyz.apex.forge.fantasydice.init.DiceType;

/**
 * Event fired when a Player rolls a Die.
 * <p>
 * This event is fired server side only.
 * <p>
 * This event is not {@link Cancelable}.
 * <p>
 * This event does not have a {@link Result}
 */
public final class DiceRollEvent extends PlayerEvent
{
    private final InteractionHand hand;
    private final ItemStack stack;
    private final DiceType<?, ?> diceType;
    private final int roll;
    private final IntList rolls;
    private final int maxPossibleRoll;

    DiceRollEvent(Player player, InteractionHand hand, ItemStack stack, DiceType<?, ?> diceType, int roll, IntList rolls, int maxPossibleRoll)
    {
        super(player);

        this.hand = hand;
        this.stack = stack;
        this.diceType = diceType;
        this.roll = roll;
        this.rolls = rolls;
        this.maxPossibleRoll = maxPossibleRoll;
    }

    /**
     * @return Hand that was right-clicked during dice roll.
     */
    public InteractionHand hand()
    {
        return hand;
    }

    /**
     * @return Dice item stack that was activated.
     */
    public ItemStack diceStack()
    {
        return stack;
    }

    /**
     * @return Type of dice.
     */
    public DiceType<?, ?> diceType()
    {
        return diceType;
    }

    /**
     * @return Sum of all individual rolls.
     */
    public int roll()
    {
        return roll;
    }

    /**
     * @return Immutable list for each individual roll (1 roll per item in stack).
     */
    public IntList rolls()
    {
        return rolls;
    }

    /**
     * @return Max possible value when all rolls summed together.
     * @apiNote Use {@code diceStack().getCount()} to retrieve the max value per roll rather than the max possible overall roll.
     */
    public int maxPossibleRoll()
    {
        return maxPossibleRoll;
    }

    public static void fireDiceRoll(Player player, InteractionHand hand, ItemStack stack, DiceType<? ,?> diceType, int roll, int[] rolls, int maxPossibleRoll)
    {
        if(player.level.isClientSide)
            return;

        MinecraftForge.EVENT_BUS.post(new DiceRollEvent(player, hand, stack, diceType, roll, IntList.of(rolls), maxPossibleRoll));
    }
}
