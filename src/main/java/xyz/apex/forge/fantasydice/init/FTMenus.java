package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.util.entry.MenuEntry;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

import xyz.apex.forge.fantasydice.client.screen.DiceStationMenuScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchMenuScreen;
import xyz.apex.forge.fantasydice.container.DiceStationMenu;
import xyz.apex.forge.fantasydice.container.PouchContainer;
import xyz.apex.forge.fantasydice.container.inventory.ItemInventory;

public final class FTMenus
{
	public static final MenuEntry<PouchContainer> POUCH = FTRegistry.INSTANCE
			.object("pouch")
			.menu(
					"pouch",
					(containerType, i, playerInventory, packetBuffer) -> new PouchContainer(containerType, i, playerInventory, packetBuffer, findPouch(playerInventory.player)),
					() -> PouchMenuScreen::new
			)
			.register();

	public static final MenuEntry<DiceStationMenu> DICE_STATION = FTRegistry.INSTANCE
			.object("dice_station")
			.menu(
					"dice_station",
					(containerType, i, playerInventory, packetBuffer) -> new DiceStationMenu(containerType, i, playerInventory, packetBuffer, ContainerLevelAccess.NULL),
					() -> DiceStationMenuScreen::new
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