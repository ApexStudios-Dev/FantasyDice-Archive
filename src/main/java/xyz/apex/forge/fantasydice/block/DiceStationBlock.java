package xyz.apex.forge.fantasydice.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import xyz.apex.forge.fantasydice.container.DiceStationMenu;
import xyz.apex.forge.fantasydice.init.FTMenus;

public class DiceStationBlock extends Block
{
	public DiceStationBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(player instanceof ServerPlayer serverPlayer)
		{
			NetworkHooks.openGui(
					serverPlayer,
					getMenuProvider(blockState, level, pos)
			);

			return InteractionResult.CONSUME;
		}

		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos pos)
	{
		var title = getName();
		return new SimpleMenuProvider(
				(windowId, playerInventory, player) -> new DiceStationMenu(FTMenus.DICE_STATION.asMenuType(), windowId, playerInventory, ContainerLevelAccess.create(level, pos)),
				title
		);
	}
}
