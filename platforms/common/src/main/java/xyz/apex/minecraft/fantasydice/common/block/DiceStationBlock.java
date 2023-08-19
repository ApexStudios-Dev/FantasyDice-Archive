package xyz.apex.minecraft.fantasydice.common.block;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import xyz.apex.minecraft.apexcore.common.lib.component.block.BaseBlockComponentHolder;
import xyz.apex.minecraft.apexcore.common.lib.component.block.BlockComponentRegistrar;
import xyz.apex.minecraft.apexcore.common.lib.component.block.types.BlockComponentTypes;
import xyz.apex.minecraft.fantasydice.common.menu.DiceStationMenu;

public final class DiceStationBlock extends BaseBlockComponentHolder
{
    public DiceStationBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void registerComponents(BlockComponentRegistrar registrar)
    {
        registrar.register(BlockComponentTypes.HORIZONTAL_FACING);
        registrar.register(BlockComponentTypes.MENU_PROVIDER, component -> component
                .withMenuConstructor((syncId, inventory, level, pos) -> DiceStationMenu.create(syncId, inventory, level instanceof Level lvl ? ContainerLevelAccess.create(lvl, pos) : ContainerLevelAccess.NULL))
        );
    }
}
