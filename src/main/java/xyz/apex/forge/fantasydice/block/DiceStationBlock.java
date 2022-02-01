package xyz.apex.forge.fantasydice.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import xyz.apex.forge.fantasydice.container.DiceStationContainer;
import xyz.apex.forge.fantasydice.init.FTContainers;

import javax.annotation.Nullable;

public class DiceStationBlock extends Block
{
	public DiceStationBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState blockState, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
	{
		if(player instanceof ServerPlayerEntity)
		{
			NetworkHooks.openGui(
					(ServerPlayerEntity) player,
					getMenuProvider(blockState, level, pos)
			);

			return ActionResultType.CONSUME;
		}

		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	public INamedContainerProvider getMenuProvider(BlockState blockState, World level, BlockPos pos)
	{
		IFormattableTextComponent title = getName();
		return new SimpleNamedContainerProvider(
				(windowId, playerInventory, player) -> new DiceStationContainer(FTContainers.DICE_STATION.asContainerType(), windowId, playerInventory, IWorldPosCallable.create(level, pos)),
				title
		);
	}
}
