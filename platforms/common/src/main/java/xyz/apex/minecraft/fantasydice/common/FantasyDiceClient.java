package xyz.apex.minecraft.fantasydice.common;

import org.jetbrains.annotations.ApiStatus;
import xyz.apex.lib.Services;
import xyz.apex.minecraft.apexcore.common.lib.PhysicalSide;
import xyz.apex.minecraft.apexcore.common.lib.SideOnly;

@ApiStatus.NonExtendable
@SideOnly(PhysicalSide.CLIENT)
public interface FantasyDiceClient
{
    FantasyDiceClient INSTANCE = Services.singleton(FantasyDiceClient.class);

    default void bootstrap()
    {
    }
}
