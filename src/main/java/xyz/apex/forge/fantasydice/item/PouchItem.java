package xyz.apex.forge.fantasydice.item;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import xyz.apex.forge.fantasydice.container.PouchContainer;
import xyz.apex.forge.fantasydice.init.FTMenus;

public class PouchItem extends Item implements DyeableLeatherItem
{
	public PouchItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);

		if(player instanceof ServerPlayer serverPlayer)
		{
			if(!openContainerScreen(level, serverPlayer, hand, stack, stack.getHoverName()))
				return InteractionResultHolder.pass(stack);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	protected boolean openContainerScreen(Level level, ServerPlayer player, InteractionHand hand, ItemStack stack, Component titleComponent)
	{
		NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, plr) -> {
			var buffer = new FriendlyByteBuf(Unpooled.buffer());
			buffer.writeBlockPos(player.blockPosition());
			buffer.writeEnum(hand);
			return new PouchContainer(FTMenus.POUCH.get(), windowId, playerInventory, buffer);
		}, titleComponent), buffer -> buffer.writeBlockPos(player.blockPosition()).writeEnum(hand));
		return true;
	}
}