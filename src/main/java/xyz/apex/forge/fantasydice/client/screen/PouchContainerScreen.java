package xyz.apex.forge.fantasydice.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.container.PouchContainer;

public final class PouchContainerScreen extends ContainerScreen<PouchContainer>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(FantasyDice.ID, "textures/gui/container/pouch.png");

	public PouchContainerScreen(PouchContainer container, PlayerInventory playerInventory, ITextComponent title)
	{
		super(container, playerInventory, title);
	}

	@Override
	protected void init()
	{
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTick);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTick, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bind(TEXTURE);
		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		blit(pose, i, j, 0, 0, imageWidth, imageHeight);
	}
}
