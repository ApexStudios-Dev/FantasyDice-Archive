package xyz.apex.forge.fantasydice.init;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemContainer;
import xyz.apex.forge.fantasydice.client.screen.DiceStationMenuScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchMenuScreen;
import xyz.apex.forge.fantasydice.container.DiceStationMenu;
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

	public static final MenuEntry<DiceStationMenu> DICE_STATION = REGISTRY
			.container(
					"dice_station",
					(menuType, i, playerInventory, packetBuffer) -> new DiceStationMenu(menuType, i, playerInventory),
					() -> DiceStationMenuScreen::new
			)
			.register();

	static void bootstrap()
	{
	}

	// TODO: Move to ApexCore and make more generic (for any ItemInventory not just DicePouch)
	private static ItemContainer findPouch(Player player)
	{
		for(var hand : InteractionHand.values())
		{
			var stack = player.getItemInHand(hand);

			if(FTItems.POUCH.isInStack(stack))
				return new ItemContainer(stack, 18);
		}

		throw new IllegalStateException("Player is not holding a Dice Pouch, they *SHOULD* be!!");
	}
}