package xyz.apex.forge.fantasydice.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import xyz.apex.forge.commonality.init.Mods;
import xyz.apex.forge.fantasydice.container.DiceStationContainer;

public class DiceStationContainerScreen extends AbstractContainerScreen<DiceStationContainer>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Mods.FANTASY_DICE, "textures/gui/container/dice_station.png");

	private float scrollOffset = 0F;
	private boolean scrolling = false;
	private int startIndex = 0;
	private boolean displayRecipes = false;

	public DiceStationContainerScreen(DiceStationContainer container, Inventory playerInventory, Component title)
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
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = leftPos;
		var j = topPos;
		blit(pose, i, j, 0, 0, imageWidth, imageHeight);
		var k = (int) (41F * scrollOffset);
		blit(pose, i + 119, j + 15 + k, 176 + (isScrollBarActive() ? 0 : 12), 0, 12, 15);
		var l = leftPos + 52;
		var i1 = topPos + 14;
		var j1 = + startIndex + 12;
		renderButtons(pose, mouseX, mouseY, l, i1, j1);
		renderRecipes(l, i1, j1);
	}

	@Override
	protected void renderTooltip(PoseStack pose, int x, int y)
	{
		super.renderTooltip(pose, x, y);

		if(displayRecipes)
		{
			var i = leftPos + 52;
			var j = topPos + 14;
			var k = startIndex + 12;
			var recipes = menu.getRecipes();

			for(var l = startIndex; l < k && l < recipes.size(); l++)
			{
				var i1 = l - startIndex;
				var j1 = i + i1 % 4 * 16;
				var k1 = j + i1 / 4 * 18 + 2;

				if(x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18)
					renderTooltip(pose, recipes.get(l).getResultItem(), x, y);
			}
		}
	}

	private void renderButtons(PoseStack pose, int mouseX, int mouseY, int x, int y, int lastVisibleElementIndex)
	{
		for(var i = startIndex; i < lastVisibleElementIndex && i < menu.getNumRecipes(); i++)
		{
			var j = i - startIndex;
			var k = x + j % 4 * 16;
			var l = j / 4;
			var i1 = y + l * 18 + 2;
			var j1 = imageHeight;

			if(i == menu.getSelectedRecipeIndex())
				j1 += 18;
			else if(mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18)
				j1 += 36;

			blit(pose, k, i1 - 1, 0, j1, 16, 18);
		}
	}

	private void renderRecipes(int left, int top, int recipeIndexOffsetMax)
	{
		var recipes = menu.getRecipes();

		for(var i = startIndex; i < recipeIndexOffsetMax && i < menu.getNumRecipes(); i++)
		{
			var j = i - startIndex;
			var k = left + j % 4 * 16;
			var l = j / 4;
			var i1 = top + l * 18 + 2;

			minecraft.getItemRenderer().renderAndDecorateItem(recipes.get(i).getResultItem(), k, i1);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		scrolling = false;

		if(displayRecipes)
		{
			var i = leftPos + 52;
			var j = topPos + 14;
			var k = startIndex + 12;

			for(var l = startIndex; l < k; l++)
			{
				var i1 = l - startIndex;
				var d0 = mouseX - (double) (i + i1 % 4 * 16);
				var d1 = mouseY - (double) (j + i1 / 4 * 18);

				if(d0 >= 0D && d1 >= 0D && d0 < 16D && d1 < 18D && menu.clickMenuButton(minecraft.player, l))
				{
					minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1F));
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
			var i = topPos + 14;
			var j = i + 54;

			scrollOffset = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15F);
			scrollOffset = Mth.clamp(scrollOffset, 0F, 1F);
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
			var i = getOffscreenRows();
			scrollOffset = (float) ((double) (scrollOffset - scrollDelta / (double) i));
			scrollOffset = Mth.clamp(scrollOffset, 0F, 1F);
			startIndex = (int) ((double) (scrollOffset * (float) i) + .5D) * 4;
		}

		return true;
	}
}
