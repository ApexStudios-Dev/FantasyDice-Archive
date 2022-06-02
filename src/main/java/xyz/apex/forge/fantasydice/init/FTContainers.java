package xyz.apex.forge.fantasydice.init;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.client.screen.DiceStationContainerScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchContainerScreen;
import xyz.apex.forge.fantasydice.container.DiceStationContainer;
import xyz.apex.forge.fantasydice.container.PouchContainer;
import xyz.apex.forge.utility.registrator.entry.MenuEntry;

public final class FTContainers
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final MenuEntry<PouchContainer> POUCH = REGISTRY
			.container(
					"pouch",
					(containerType, i, playerInventory, packetBuffer) -> new PouchContainer(containerType, i, playerInventory, findPouch(playerInventory.player)),
					() -> PouchContainerScreen::new
			)
			.register();

	public static final MenuEntry<DiceStationContainer> DICE_STATION = REGISTRY
			.container(
					"dice_station",
					(containerType, i, playerInventory, packetBuffer) -> new DiceStationContainer(containerType, i, playerInventory),
					() -> DiceStationContainerScreen::new
			)
			.register();

	static void bootstrap()
	{
	}

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
