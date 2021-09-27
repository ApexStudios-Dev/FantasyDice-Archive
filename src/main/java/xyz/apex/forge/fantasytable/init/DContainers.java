package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.util.entry.ContainerEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.client.screen.PouchContainerScreen;
import xyz.apex.forge.fantasytable.container.PouchContainer;
import xyz.apex.forge.fantasytable.item.PouchItem;

public final class DContainers
{
	// formatter:off
	public static final ContainerEntry<PouchContainer> POUCH_CONTAINER = FantasyTable
			.registrate()
			.object(DStrings.CONTAINER_POUCH)
			.container((type, windowId, inv) -> new PouchContainer(type, windowId, inv, findPouch(inv.player)), () -> PouchContainerScreen::new)
			.register();
	// formatter:on

	@Deprecated //internal use only
	public static void register() { }

	private static PouchItem.Inv findPouch(PlayerEntity player)
	{
		for(Hand hand : Hand.values())
		{
			ItemStack stack = player.getItemInHand(hand);

			if(DItems.POUCH_ITEM.is(stack.getItem()))
				return new PouchItem.Inv(stack);
		}

		throw new IllegalStateException("Player is not holding a Dice Pouch, they *SHOULD* be!!");
	}
}
