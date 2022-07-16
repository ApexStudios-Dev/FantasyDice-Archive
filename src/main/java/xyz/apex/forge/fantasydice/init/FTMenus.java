package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.util.entry.MenuEntry;

import xyz.apex.forge.fantasydice.client.screen.DiceStationMenuScreen;
import xyz.apex.forge.fantasydice.client.screen.PouchMenuScreen;
import xyz.apex.forge.fantasydice.container.DiceStationMenu;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class FTMenus
{
	public static final MenuEntry<PouchContainer> POUCH = FTRegistry.INSTANCE
			.object("pouch")
			.menu(
					"pouch", PouchContainer::new,
					() -> PouchMenuScreen::new
			)
			.register();

	public static final MenuEntry<DiceStationMenu> DICE_STATION = FTRegistry.INSTANCE
			.object("dice_station")
			.menu(
					"dice_station", DiceStationMenu::new,
					() -> DiceStationMenuScreen::new
			)
			.register();

	static void bootstrap()
	{
	}
}