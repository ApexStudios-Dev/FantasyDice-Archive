package xyz.apex.minecraft.fantasydice.fabric.entrypoint;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.apexcore.common.lib.resgen.ApexDataProvider;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;

@ApiStatus.Internal
public final class FantasyDiceDataGeneratorEntrypoint implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        ApexDataProvider.register(FantasyDice.ID, func -> generator.createPack().addProvider(func::apply));
    }
}
