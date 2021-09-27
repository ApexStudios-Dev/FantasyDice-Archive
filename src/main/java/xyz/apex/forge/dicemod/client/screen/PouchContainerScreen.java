package xyz.apex.forge.dicemod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.container.PouchContainer;
import xyz.apex.forge.dicemod.init.DStrings;

@OnlyIn(Dist.CLIENT)
public class PouchContainerScreen extends ContainerScreen<PouchContainer>
{
	private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation(DiceMod.ID, DStrings.CONTAINER_POUCH_TEXTURE);

	public PouchContainerScreen(PouchContainer container, PlayerInventory playerInventory, ITextComponent textComponent)
	{
		super(container, playerInventory, textComponent);
	}

	@Override
	protected void init()
	{
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bind(CONTAINER_LOCATION);
		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		blit(matrixStack, i, j, 0, 0, imageWidth, imageHeight);
	}
}
