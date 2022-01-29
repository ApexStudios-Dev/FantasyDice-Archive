package xyz.apex.forge.fantasydice.init;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import xyz.apex.forge.apexcore.lib.container.inventory.ItemInventory;
import xyz.apex.forge.fantasydice.client.screen.PouchContainerScreen;
import xyz.apex.forge.fantasydice.container.PouchContainer;
import xyz.apex.forge.utility.registrator.entry.ContainerEntry;

public final class FTContainers
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final ContainerEntry<PouchContainer> POUCH = REGISTRY
			.container(
					"pouch",
					(containerType, i, playerInventory, packetBuffer) -> new PouchContainer(containerType, i, playerInventory, findPouch(playerInventory.player)),
					() -> PouchContainerScreen::new
			)
			.register();

	static void bootstrap()
	{
	}

	// TODO: Move to ApexCore and make more generic (for any ItemInventory not just DicePouch)
	private static ItemInventory findPouch(PlayerEntity player)
	{
		for(Hand hand : Hand.values())
		{
			ItemStack stack = player.getItemInHand(hand);

			if(FTItems.POUCH.isInStack(stack))
				return new ItemInventory(stack, 18);
		}

		throw new IllegalStateException("Player is not holding a Dice Pouch, they *SHOULD* be!!");
	}
}
