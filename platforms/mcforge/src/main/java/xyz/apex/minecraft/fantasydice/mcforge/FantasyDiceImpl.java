package xyz.apex.minecraft.fantasydice.mcforge;

import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.PhysicalSide;
import xyz.apex.minecraft.apexcore.mcforge.lib.EventBuses;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.FantasyDiceClient;

@ApiStatus.Internal
public final class FantasyDiceImpl implements FantasyDice
{
    @Override
    public void bootstrap()
    {
        FantasyDice.super.bootstrap();
        EventBuses.registerForJavaFML();
        PhysicalSide.CLIENT.runWhenOn(() -> FantasyDiceClient.INSTANCE::bootstrap);
    }
}
