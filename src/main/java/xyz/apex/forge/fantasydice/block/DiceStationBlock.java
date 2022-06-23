package xyz.apex.forge.fantasydice.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import xyz.apex.forge.fantasydice.container.DiceStationContainer;
import xyz.apex.forge.fantasydice.init.FTContainers;

import javax.annotation.Nullable;

public class DiceStationBlock extends Block
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public DiceStationBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
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
				(windowId, playerInventory, player) -> new DiceStationContainer(FTContainers.DICE_STATION.get(), windowId, playerInventory, ContainerLevelAccess.create(level, pos)),
				title
		);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation)
	{
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror)
	{
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}
}
