package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.util.entry.MenuEntry;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.client.screen.DiceStationContainerScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchContainerScreen;
import xyz.apex.forge.fantasydice.container.DiceStationContainer;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class FTContainers
{
	public static final MenuEntry<PouchContainer> POUCH = FTRegistry.INSTANCE
			.object("pouch")
			.menu(
					"pouch",
					(containerType, i, playerInventory, packetBuffer) -> new PouchContainer(containerType, i, playerInventory, findPouch(playerInventory.player)),
					() -> PouchContainerScreen::new
			)
			.register();

	public static final MenuEntry<DiceStationContainer> DICE_STATION = FTRegistry.INSTANCE
			.object("dice_station")
			.menu(
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

			if(FTItems.POUCH.isIn(stack))
				return new ItemInventory(stack, 18);
		}

		throw new IllegalStateException("Player is not holding a Dice Pouch, they *SHOULD* be!!");
	}
}
