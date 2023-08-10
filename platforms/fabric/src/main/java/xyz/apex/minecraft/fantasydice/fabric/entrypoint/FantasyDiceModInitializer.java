package xyz.apex.minecraft.fantasydice.fabric.entrypoint;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;

@ApiStatus.Internal
public final class FantasyDiceModInitializer implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        FantasyDice.INSTANCE.bootstrap();
    }
}
