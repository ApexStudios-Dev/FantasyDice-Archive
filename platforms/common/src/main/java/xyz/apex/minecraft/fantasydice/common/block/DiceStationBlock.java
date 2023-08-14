package xyz.apex.minecraft.fantasydice.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xyz.apex.minecraft.apexcore.common.lib.component.block.BaseBlockComponentHolder;
import xyz.apex.minecraft.apexcore.common.lib.component.block.BlockComponentRegistrar;
import xyz.apex.minecraft.apexcore.common.lib.component.block.types.BlockComponentTypes;
import xyz.apex.minecraft.apexcore.common.lib.helper.InteractionResultHelper;
import xyz.apex.minecraft.apexcore.common.lib.hook.MenuHooks;
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
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        var result = super.use(blockState, level, pos, player, hand, hit);

        if(result.consumesAction())
            return result;

        MenuHooks.get().openMenu(player, getName(), (syncId, inventory, $) -> DiceStationMenu.create(syncId, inventory, ContainerLevelAccess.create(level, pos)), this::writeExtraData);
        return InteractionResultHelper.BlockUse.succeedAndSwingArmBothSides(level.isClientSide);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos pos)
    {
        return MenuHooks.get().createMenuProvider(getName(), (syncId, inventory, player) -> DiceStationMenu.create(syncId, inventory, ContainerLevelAccess.create(level, pos)), this::writeExtraData);
    }

    private void writeExtraData(FriendlyByteBuf buffer)
    {
    }
}
