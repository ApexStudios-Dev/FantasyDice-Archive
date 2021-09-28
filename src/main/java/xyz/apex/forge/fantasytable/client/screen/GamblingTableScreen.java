package xyz.apex.forge.fantasytable.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xyz.apex.forge.fantasytable.container.GamblingTableContainer;

public class GamblingTableScreen extends ContainerScreen<GamblingTableContainer>
{
	private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

	public GamblingTableScreen(GamblingTableContainer container, PlayerInventory playerInventory, ITextComponent textComponent)
	{
		super(container, playerInventory, textComponent);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bind(CRAFTING_TABLE_LOCATION);
		int j = (height - imageHeight) / 2;
		blit(matrixStack, leftPos, j, 0, 0, imageWidth, imageHeight);
	}
}
