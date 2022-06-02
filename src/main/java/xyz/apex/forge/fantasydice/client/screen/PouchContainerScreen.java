package xyz.apex.forge.fantasydice.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import xyz.apex.forge.apexcore.lib.client.screen.ItemInventoryContainerScreen;
import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class PouchContainerScreen extends ItemInventoryContainerScreen<PouchContainer>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(FantasyDice.ID, "textures/gui/container/pouch.png");

	public PouchContainerScreen(PouchContainer container, Inventory playerInventory, Component title)
	{
		super(container, playerInventory, title, TEXTURE);
	}
}
