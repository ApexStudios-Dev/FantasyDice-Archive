package xyz.apex.minecraft.fantasydice.neoforge;

import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.PhysicalSide;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.FantasyDiceClient;

@ApiStatus.Internal
@Mod(FantasyDice.ID)
public final class FantasyDiceForgeEP
{
    public FantasyDiceForgeEP()
    {
        FantasyDice.INSTANCE.bootstrap();
        PhysicalSide.CLIENT.runWhenOn(() -> FantasyDiceClient.INSTANCE::bootstrap);
    }
}
