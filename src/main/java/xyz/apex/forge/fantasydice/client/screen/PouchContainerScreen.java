package xyz.apex.forge.fantasydice.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import xyz.apex.forge.apexcore.lib.client.screen.ItemInventoryContainerScreen;
import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class PouchContainerScreen extends ItemInventoryContainerScreen<PouchContainer>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(FantasyDice.ID, "textures/gui/container/pouch.png");

	public PouchContainerScreen(PouchContainer container, PlayerInventory playerInventory, ITextComponent title)
	{
		super(container, playerInventory, title, TEXTURE);
	}
}
