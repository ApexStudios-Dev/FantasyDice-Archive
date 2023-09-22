package xyz.apex.minecraft.fantasydice.mcforge;

import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;

@ApiStatus.Internal
@Mod(FantasyDice.ID)
public final class FantasyDiceMinecraftForgeEP
{
    public FantasyDiceMinecraftForgeEP()
    {
        FantasyDice.INSTANCE.bootstrap();
    }
}
