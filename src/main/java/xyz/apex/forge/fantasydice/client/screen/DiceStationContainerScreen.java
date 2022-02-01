package xyz.apex.forge.fantasydice.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import xyz.apex.forge.fantasydice.FantasyDice;
import xyz.apex.forge.fantasydice.container.DiceStationContainer;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

import java.util.List;

public class DiceStationContainerScreen extends ContainerScreen<DiceStationContainer>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(FantasyDice.ID, "textures/gui/container/dice_station.png");

	private float scrollOffset = 0F;
	private boolean scrolling = false;
	private int startIndex = 0;
	private boolean displayRecipes = false;

	public DiceStationContainerScreen(DiceStationContainer container, PlayerInventory playerInventory, ITextComponent title)
	{
		super(container, playerInventory, title);

		container.registerUpdateListener(this::containerChanged);
		--titleLabelY;
	}

	private boolean isScrollBarActive()
	{
		return displayRecipes && menu.getNumRecipes() > 12;
	}

	private int getOffscreenRows()
	{
		return (menu.getNumRecipes() + 4 - 1) / 4 - 3;
	}

	private void containerChanged()
	{
		displayRecipes = menu.hasInputItem();

		if(!displayRecipes)
		{
			scrollOffset = 0F;
			startIndex = 0;
		}
	}

	@Override
	protected void init()
	{
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.textureManager.bind(TEXTURE);
		int i = leftPos;
		int j = topPos;
		blit(pose, i, j, 0, 0, imageWidth, imageHeight);
		int k = (int) (41F * scrollOffset);
		blit(pose, i + 119, j + 15 + k, 176 + (isScrollBarActive() ? 0 : 12), 0, 12, 15);
		int l = leftPos + 52;
		int i1 = topPos + 14;
		int j1 = + startIndex + 12;
		renderButtons(pose, mouseX, mouseY, l, i1, j1);
		renderRecipes(l, i1, j1);
	}

	@Override
	protected void renderTooltip(MatrixStack pose, int x, int y)
	{
		super.renderTooltip(pose, x, y);

		if(displayRecipes)
		{
			int i = leftPos + 52;
			int j = topPos + 14;
			int k = startIndex + 12;
			List<DiceStationRecipe> recipes = menu.getRecipes();

			for(int l = startIndex; l < k && l < recipes.size(); l++)
			{
				int i1 = l - startIndex;
				int j1 = i + i1 % 4 * 16;
				int k1 = j + i1 / 4 * 18 + 2;

				if(x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18)
					renderTooltip(pose, recipes.get(l).getResultItem(), x, y);
			}
		}
	}

	private void renderButtons(MatrixStack pose, int mouseX, int mouseY, int x, int y, int lastVisibleElementIndex)
	{
		for(int i = startIndex; i < lastVisibleElementIndex && i < menu.getNumRecipes(); i++)
		{
			int j = i - startIndex;
			int k = x + j % 4 * 16;
			int l = j / 4;
			int i1 = y + l * 18 + 2;
			int j1 = imageHeight;

			if(i == menu.getSelectedRecipeIndex())
				j1 += 18;
			else if(mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18)
				j1 += 36;

			blit(pose, k, i1 - 1, 0, j1, 16, 18);
		}
	}

	private void renderRecipes(int left, int top, int recipeIndexOffsetMax)
	{
		List<DiceStationRecipe> recipes = menu.getRecipes();

		for(int i = startIndex; i < recipeIndexOffsetMax && i < menu.getNumRecipes(); i++)
		{
			int j = i - startIndex;
			int k = left + j % 4 * 16;
			int l = j / 4;
			int i1 = top + l * 18 + 2;

			minecraft.getItemRenderer().renderAndDecorateItem(recipes.get(i).getResultItem(), k, i1);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		scrolling = false;

		if(displayRecipes)
		{
			int i = leftPos + 52;
			int j = topPos + 14;
			int k = startIndex + 12;

			for(int l = startIndex; l < k; l++)
			{
				int i1 = l - startIndex;
				double d0 = mouseX - (double) (i + i1 % 4 * 16);
				double d1 = mouseY - (double) (j + i1 / 4 * 18);

				if(d0 >= 0D && d1 >= 0D && d0 < 16D && d1 < 18D && menu.clickMenuButton(minecraft.player, l))
				{
					minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1F));
					minecraft.gameMode.handleInventoryButtonClick(menu.containerId, l);
					return true;
				}
			}

			i = leftPos + 119;
			j = topPos + 9;

			if(mouseButton >= (double) i && mouseX < i + 12D && mouseY >= (double) j && mouseY < j + 54D)
				scrolling = true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
	{
		if(scrolling && isScrollBarActive())
		{
			int i = topPos + 14;
			int j = i + 54;

			scrollOffset = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15F);
			scrollOffset = MathHelper.clamp(scrollOffset, 0F, 1F);
			startIndex = (int) ((double) (scrollOffset * (float) getOffscreenRows()) + .5D) * 4;
			return true;
		}

		return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta)
	{
		if(isScrollBarActive())
		{
			int i = getOffscreenRows();
			scrollOffset = (float) ((double) (scrollOffset - scrollDelta / (double) i));
			scrollOffset = MathHelper.clamp(scrollOffset, 0F, 1F);
			startIndex = (int) ((double) (scrollOffset * (float) i) + .5D) * 4;
		}

		return true;
	}
}
