package xyz.apex.forge.fantasydice.block;

import io.netty.buffer.Unpooled;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import xyz.apex.forge.apexcore.revamp.block.BaseBlock;
import xyz.apex.forge.fantasydice.container.DiceStationMenu;
import xyz.apex.forge.fantasydice.init.FTMenus;

import java.util.function.Consumer;

public class DiceStationBlock extends BaseBlock
{
	public DiceStationBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected void registerProperties(Consumer<Property<?>> consumer)
	{
		super.registerProperties(consumer);
		consumer.accept(FACING_4_WAY);
	}

	// region: Core
	protected final InteractionResult tryOpenContainerScreen(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
	{
		var provider = getMenuProvider(blockState, level, pos);

		if(provider != null)
		{
			if(level.isClientSide)
				return InteractionResult.SUCCESS;

			if(player instanceof ServerPlayer serverPlayer)
			{
				NetworkHooks.openGui(serverPlayer, provider, buffer -> buffer.writeBlockPos(pos));
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.PASS;
	}
	// endregion

	// region: Overrides
	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
	{
		return tryOpenContainerScreen(blockState, level, pos, player, hand, rayTraceResult);
	}

	@Override
	public final MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos pos)
	{
		Component containerName = Component.translatable(getDescriptionId());

		return new SimpleMenuProvider((windowId, playerInventory, player) -> {
			var buffer = new FriendlyByteBuf(Unpooled.buffer());
			buffer.writeBlockPos(pos);
			return new DiceStationMenu(FTMenus.DICE_STATION.get(), windowId, playerInventory, buffer, ContainerLevelAccess.NULL);
		}, containerName);
	}
	// endregion
}