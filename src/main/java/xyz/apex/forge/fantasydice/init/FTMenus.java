package xyz.apex.forge.fantasydice.init;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.client.screen.PouchMenuScreen;
import xyz.apex.forge.fantasydice.container.PouchMenu;
import xyz.apex.forge.utility.registrator.entry.MenuEntry;

public final class FTMenus
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final MenuEntry<PouchMenu> POUCH = REGISTRY
			.container(
					"pouch",
					(menuType, i, playerInventory, packetBuffer) -> new PouchMenu(menuType, i, playerInventory, findPouch(playerInventory.player)),
					() -> PouchMenuScreen::new
			)
			.register();

	static void bootstrap()
	{
	}

	// TODO: Move to ApexCore and make more generic (for any ItemInventory not just DicePouch)
	private static ItemInventory findPouch(Player player)
	{
		for(var hand : InteractionHand.values())
		{
			var stack = player.getItemInHand(hand);

			if(FTItems.POUCH.isInStack(stack))
				return new ItemInventory(stack, 18);
		}

		throw new IllegalStateException("Player is not holding a Dice Pouch, they *SHOULD* be!!");
	}
}
