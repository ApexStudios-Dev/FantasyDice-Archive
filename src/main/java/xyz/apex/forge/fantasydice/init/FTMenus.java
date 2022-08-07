package xyz.apex.forge.fantasydice.init;

import xyz.apex.forge.apexcore.registrate.entry.MenuEntry;
import xyz.apex.forge.fantasydice.client.screen.DiceStationMenuScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchMenuScreen;
import xyz.apex.forge.fantasydice.container.DiceStationMenu;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class FTMenus
{
	public static final MenuEntry<PouchContainer> POUCH = FTRegistry
			.REGISTRATE
			.object("pouch")
			.menu(
					PouchContainer::new,
					() -> PouchMenuScreen::new
			)
	;

	public static final MenuEntry<DiceStationMenu> DICE_STATION = FTRegistry
			.REGISTRATE
			.object("dice_station")
			.menu(
					DiceStationMenu::new,
					() -> DiceStationMenuScreen::new
			)
	;

	static void bootstrap()
	{
	}
}