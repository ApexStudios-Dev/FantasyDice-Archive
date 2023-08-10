package xyz.apex.minecraft.fantasydice.neoforge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.neoforge.lib.EventBusHelper;
import xyz.apex.minecraft.apexcore.neoforge.lib.EventBuses;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;

@ApiStatus.Internal
public final class FantasyDiceImpl implements FantasyDice
{
    @Override
    public void bootstrap()
    {
        FantasyDice.super.bootstrap();
        EventBuses.registerForJavaFML();
    }
}
