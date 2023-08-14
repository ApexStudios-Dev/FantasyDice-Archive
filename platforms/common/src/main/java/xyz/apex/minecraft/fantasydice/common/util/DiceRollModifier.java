package xyz.apex.minecraft.fantasydice.common.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@FunctionalInterface
public interface DiceRollModifier
{
    int modify(Player roller, ItemStack dice, int sides, int originalRoll);
}
