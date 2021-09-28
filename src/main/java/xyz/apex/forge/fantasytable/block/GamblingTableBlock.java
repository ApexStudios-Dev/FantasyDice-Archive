package xyz.apex.forge.fantasytable.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import xyz.apex.forge.fantasytable.init.FContainers;

public class GamblingTableBlock extends Block
{
	public GamblingTableBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
	{
		if(player instanceof ServerPlayerEntity)
		{
			FContainers.GAMBLING_TABLE.open(
					(ServerPlayerEntity) player,
					getName()
			);

			return ActionResultType.CONSUME;
		}

		return ActionResultType.SUCCESS;
	}
}
