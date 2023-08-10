package xyz.apex.minecraft.fantasydice.fabric.entrypoint;

import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.fantasydice.common.FantasyDiceClient;

@ApiStatus.Internal
public final class FantasyDiceClientModInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        FantasyDiceClient.INSTANCE.bootstrap();
    }
}
